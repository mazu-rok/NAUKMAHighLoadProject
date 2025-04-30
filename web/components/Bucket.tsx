import { formatDate } from "@/components/helpers/formatDate";
import { EventResponse } from "@/components/types/event";
import { Badge, Box, Button, Card, Group, Modal, Stack, Text, Title } from "@mantine/core";
import { IconX } from "@tabler/icons-react";
import React, { useEffect, useState } from "react";


interface PlaceItem {
  "placeId": string,
  "eventId": string,
  "row": number,
  "place": number,
  "status": string,
  event: EventResponse
}

interface PlaceProps { place:PlaceItem, removePlace: (data: { placeId: string, eventId: string }) => void}


const Place = ({ place, removePlace }: PlaceProps) => {
  const { placeId, eventId, row, status } = place;
  const onRemove = async () => {
    const headers: Record<string, string> = {};
    if (localStorage.getItem("accessToken") != null) {
      headers["Authorization"] = `Bearer ${localStorage.getItem("accessToken")}`;
    }

    const res = await fetch(`/api/buckets/${localStorage.getItem("userId")}?placeId=${placeId}&eventId=${eventId}`, {
      method: "DELETE",
      headers,
      body: JSON.stringify({placeId: placeId, eventId: eventId}),
    });

    if (res.ok) {
      removePlace({eventId, placeId});
    }
  };

  return <Card shadow="sm" padding="lg" radius="md" withBorder w="49%">
    <Stack gap={0}>
      <Title mb={4} order={3}>{place?.event?.title}</Title>
      <Title order={4} mb={20}>{place?.event?.description}</Title>

      <Text mb={4}>
        {formatDate(place?.event?.startTime)}
      </Text>

      <Group>
        <Text>
          Row: {row}
        </Text>
        <Text>
          Place: {place.place}
        </Text>
        <Text>
          <Badge>{status}</Badge>
        </Text>
      </Group>



      <Box
          style={{
            position: "absolute",
            top: 12,
            right: 12,
            cursor: 'pointer'
          }}
          onClick={onRemove}
      >
        <IconX color="black"/>
      </Box>
    </Stack>
  </Card>;
};

interface Props {
  opened: boolean;
  close: () => void;
}


const Bucket = ({ opened, close }: Props) => {
  const [places, setPlaces] = useState<PlaceItem[]>([]);

  useEffect(() => {
    if (!opened) return;
    const getOrders = async () => {
      const headers: Record<string, string> = {};
      if (localStorage.getItem("accessToken") != null) {
        headers["Authorization"] = `Bearer ${localStorage.getItem("accessToken")}`;
      }

      const res = await fetch(`/api/orders?userId=${localStorage.getItem("userId")}`, { headers });

      if (res.ok) {
        const data = await res.json();
        const newData:PlaceItem[] = []
        for (const item of data.tickets) {
          const res = await fetch(`/api/place/${item.placeId}`, {
            headers: {
              "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            },
            method: "GET"
          });
          const place = await res.json();

          const res2 = await fetch(`/api/event/${item.eventId}`, {
            headers: {
              "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
            },
            method: "GET"
          });
          place.event = await res2.json();

          newData.push(place);
        }
        setPlaces(newData);
      }

    };

    getOrders();
  }, [opened]);

  const onBuy = async () => {
    try {
      const res = await fetch(`/api/buckets/${localStorage.getItem("userId")}/orders`, {
        headers: {
          "Authorization": `Bearer ${localStorage.getItem("accessToken")}`
        },
        method: "POST"
      });
      if (!res.ok) throw new Error("Failed to buy ticket");
      setPlaces([]);
    } catch (error) {
      console.error("Error loading orders:", error);
    }
  };
  return (
      <Modal opened={opened} onClose={close} title={"Кошик " + places.length} centered size="80%">
        <Group justify="space-evenly">
          {places.map(({ placeId, eventId, ...place }) => (
              <Place
                  key={placeId}
                  place={{ placeId, eventId, ...place }}
                  removePlace={({placeId, eventId}) => {
                    setPlaces(places.filter(place => !(place.placeId === placeId && place.eventId === eventId)));
                  }}
              />
          ))}
        </Group>

        <Button color="blue" fullWidth mt="md" radius="md" onClick={onBuy} disabled={places.length === 0}>
          Купити Квитки
        </Button>
      </Modal>
  );
};

export default Bucket;