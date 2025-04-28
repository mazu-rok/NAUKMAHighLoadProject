'use client'
import { formatDate } from "@/components/helpers/formatDate";
import { EventResponse, EventStatus } from "@/components/types/event";
import { Badge, Button, Card, Group, Image, Text } from "@mantine/core";
import { useRouter } from "next/navigation";
import { calculateDurationMinutes, formatDuration } from "@/components/helpers/formatDuration";

interface EventCardProps {
  event: EventResponse;
}

export function EventCard({ event }: EventCardProps) {
  const router = useRouter();
  
  const getStatusColor = (status: EventStatus) => {
    switch (status) {
      case "DRAFT": return "gray";
      case "SCHEDULED": return "green";
      case "ENDED": return "red";
      default: return "gray";
    }
  };

  return (
    <Card
      shadow="sm"
      padding="lg"
      radius="md"
      withBorder
      w={320}
      h={400}
      style={{ 
        cursor: 'pointer',
        display: 'flex',
        flexDirection: 'column'
      }}
      onClick={() => router.push('/events/' + event.eventId)}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = "scale(1.02)";
        e.currentTarget.style.transition = "transform 0.2s";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = "scale(1)";
        e.currentTarget.style.transition = "transform 0.2s";
      }}
    >
      <Card.Section>
        <Image
          src={event.imagesMetadata[0]?.url || "/placeholder.jpg"}
          alt={event.title}
          height={160}
          fit="cover"
        />
      </Card.Section>

      <Group justify="space-between" mt="md" mb="xs">
        <Text fw={500} lineClamp={1} style={{ flex: 1 }}>{event.title}</Text>
        <Badge color={getStatusColor(event.status)}>{event.status}</Badge>
      </Group>

      <Text size="sm" c="dimmed" lineClamp={2} style={{ minHeight: '42px', marginBottom: '12px' }}>
        {event.description || "No description available."}
      </Text>

      <Group gap="xs" align="center">
        <Text size="s">{formatDate(event.startTime)}</Text>
      </Group>
      
      {event.endTime && (
        <Text size="s">
          {formatDuration(calculateDurationMinutes(event.startTime, event.endTime))}
        </Text>
      )}
      
      <Button color="blue" fullWidth mt="auto" radius="md">
        View details
      </Button>
    </Card>
  );
}