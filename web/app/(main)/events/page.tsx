'use client'
import { EventCard } from "@/components/common/events/EventCard";
import { EventResponse, EventStatus } from "@/components/types/event";
import { Center, Group, Loader, Paper, Pagination, Select, Text, Title } from "@mantine/core";
import { useEffect, useState } from "react";

type StatusFilterType = EventStatus | "ALL";

export default function EventsPage() {
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [statusFilter, setStatusFilter] = useState<StatusFilterType>("ALL");

  const fetchEvents = async (currentPage: number, size: number, filter: StatusFilterType) => {
    try {
      setLoading(true);
      setError(null);

      const query = new URLSearchParams({
        page: (currentPage - 1).toString(),
        size: size.toString(),
        sortBy: "createdAt",
        direction: "desc",
      });

      if (filter !== "ALL") {
        query.append("statuses", filter);
      }

      const res = await fetch(`/api/events?${query.toString()}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.error || "Failed to fetch events");
      }

      const data = await res.json();
      setEvents(data.content || []);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    } catch (err: unknown) {
      const errorMessage =
        err instanceof Error ? err.message : "Unknown error";
      console.error("Error fetching events:", errorMessage);
      setError(errorMessage);
    }finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEvents(page, pageSize, statusFilter);
  }, [page, pageSize, statusFilter]);

  const handleStatusChange = (value: string | null) => {
    const newFilter = (value as StatusFilterType) || "ALL";
    setStatusFilter(newFilter);
    setPage(1);
  };

  const handlePageSizeChange = (value: string | null) => {
    const newSize = parseInt(value || "10", 10);
    setPageSize(newSize);
    setPage(1);
  };

  if (loading && events.length === 0) {
    return (
      <Center h="100vh">
        <Loader />
      </Center>
    );
  }

  if (error) {
    return (
      <Center h="100vh">
        <Text c="red">{error}</Text>
      </Center>
    );
  }

  return (
    <Paper p="xl">
      <Group justify="space-between" mb="lg">
        <Title order={2}>Events</Title>
        <Group>
          <Select
            label="Filter by status"
            value={statusFilter}
            onChange={handleStatusChange}
            data={[
              { value: "ALL", label: "All Statuses" },
              { value: "DRAFT", label: "Draft" },
              { value: "SCHEDULED", label: "Scheduled" },
              { value: "ENDED", label: "Ended" }
            ]}
            w={200}
          />
          <Select
            label="Items per page"
            value={pageSize.toString()}
            onChange={handlePageSizeChange}
            data={[
              { value: "10", label: "10" },
              { value: "20", label: "20" },
              { value: "30", label: "30" },
              { value: "50", label: "50" }
            ]}
            w={100}
          />
        </Group>
      </Group>

      {loading && (
        <Center my="md">
          <Loader size="sm" />
        </Center>
      )}

      {events.length === 0 ? (
        <Text ta="center" py="xl">No events found.</Text>
      ) : (
        <Group gap="md" align="stretch" justify="center" wrap="wrap">
          {events.map((event) => (
            <EventCard key={event.eventId} event={event} />
          ))}
        </Group>
      )}

      {totalPages > 1 && (
        <Center mt="xl">
          <Group>
            <Text size="sm" c="dimmed">
              Showing {events.length} of {totalElements} events
            </Text>
            <Pagination
              total={totalPages}
              value={page}
              onChange={setPage}
              withEdges
              siblings={1}
            />
          </Group>
        </Center>
      )}
    </Paper>
  );
}