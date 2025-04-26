// components/events/EventDetailCard.tsx
"use client";

import { EventResponse, EventStatus } from "@/components/types/event";
import {
  Badge,
  Box,
  Button,
  Group,
  Grid,
  Image,
  Paper,
  Text,
  Title,
} from "@mantine/core";
import { IconCalendar, IconClock, IconMapPin } from "@tabler/icons-react";
import { formatDate } from "@/components/helpers/formatDate";
import ImageCarousel from "@/components/common/ImageCarousel";
import {
  calculateDurationMinutes,
  formatDuration,
} from "@/components/helpers/formatDuration";

interface EventDetailCardProps {
  event: EventResponse;
  onBack: () => void;
  onEdit?: () => void;
}

export default function EventDetailCard({
  event,
  onBack,
  onEdit,
}: EventDetailCardProps) {
  const getStatusColor = (status: EventStatus) => {
    switch (status) {
      case "DRAFT":
        return "gray";
      case "SCHEDULED":
        return "green";
      case "ENDED":
        return "red";
      default:
        return "gray";
    }
  };

  return (
    <Paper p="xl" radius="md" withBorder shadow="sm">
      <Button
        mb="lg"
        variant="subtle"
        leftSection={<IconCalendar size={16} />}
        onClick={onBack}
      >
        Back to Events
      </Button>

      <Grid>
        <Grid.Col span={{ base: 12, md: 6 }}>
          <Box mb="md">
            {event.imagesMetadata && event.imagesMetadata.length === 1 ? (
              <Image
                src={event.imagesMetadata[0]?.url}
                alt={event.title}
                radius="md"
                height={300}
              />
            ) : (
              <ImageCarousel images={event.imagesMetadata || []} height={340} />
            )}
          </Box>
        </Grid.Col>

        <Grid.Col span={{ base: 12, md: 6 }}>
          <Box>
            <Group justify="space-between" mb="md">
              <Title order={2}>{event.title}</Title>
              <Badge size="lg" color={getStatusColor(event.status)}>
                {event.status}
              </Badge>
            </Group>

            <Text size="sm" c="dimmed" mb="lg">
              {event.description || "No description available."}
            </Text>

            <Group gap="md" mb="md">
              <IconClock
                size={18}
                style={{ color: "var(--mantine-color-blue-6)" }}
              />
              <Text fw={400}>{formatDate(event.startTime)}</Text>
            </Group>

            {event.endTime && (
              <Group gap="md" mb="md">
                <IconClock
                  size={18}
                  style={{ color: "var(--mantine-color-blue-6)" }}
                />
                <Text size="s">
                  {formatDuration(
                    calculateDurationMinutes(event.startTime, event.endTime)
                  )}
                </Text>
              </Group>
            )}

            {event.locationAddress && (
              <Group gap="md" mb="md">
                <IconMapPin
                  size={18}
                  style={{ color: "var(--mantine-color-blue-6)" }}
                />
                <Text>{event.locationAddress}</Text>
              </Group>
            )}
          </Box>
        </Grid.Col>
      </Grid>

      {event.status === "DRAFT" && (
        <Group mt="xl" justify="flex-end">
          {onEdit && (
            <Button color="yellow" onClick={onEdit}>
              Edit Event
            </Button>
          )}
        </Group>
      )}
    </Paper>
  );
}
