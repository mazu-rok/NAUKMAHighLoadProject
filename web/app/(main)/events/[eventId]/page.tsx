"use client";

import { EventResponse } from "@/components/types/event";
import {
  Button,
  Center,
  Container,
  Loader,
  Paper,
  Text
} from "@mantine/core";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import EventDetailCard from "@/components/common/events/EventDetailCard";

export default function EventPage() {
  const params = useParams<{ eventId: string }>();
  const eventId = params?.eventId || "";
  const router = useRouter();
  const [event, setEvent] = useState<EventResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchEvent = async () => {
      try {
        setLoading(true);
        setError(null);

        const res = await fetch(`/api/event/${eventId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
          },
        });

        if (!res.ok) {
          const err = await res.json();
          throw new Error(err.error || "Failed to fetch event details");
        }

        const data = await res.json();
        setEvent(data);
      } catch (err: unknown) {
        const errorMessage =
          err instanceof Error ? err.message : "Unknown error";
        console.error("Error fetching event:", errorMessage);
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    if (eventId) {
      fetchEvent();
    }
  }, [eventId]);

  if (loading) {
    return (
      <Center h="100vh">
        <Loader />
      </Center>
    );
  }

  if (error) {
    return (
      <Center h="100vh">
        <Paper p="xl" w="100%" maw={600} withBorder>
          <Text c="red" ta="center" fw={500}>
            {error}
          </Text>
          <Button
            mt="lg"
            color="blue"
            fullWidth
            onClick={() => router.push("/events")}
          >
            Back to Events
          </Button>
        </Paper>
      </Center>
    );
  }

  if (!event) {
    return (
      <Center h="100vh">
        <Paper p="xl" w="100%" maw={600} withBorder>
          <Text ta="center">Event not found.</Text>
          <Button
            mt="lg"
            color="blue"
            fullWidth
            onClick={() => router.push("/events")}
          >
            Back to Events
          </Button>
        </Paper>
      </Center>
    );
  }

  return (
    <Container size="lg" py="xl">
      <EventDetailCard 
        event={event}
        onBack={() => router.push("/events")}
        onEdit={() => router.push(`/events/${event.eventId}/edit`)}
      />
    </Container>
  );
}