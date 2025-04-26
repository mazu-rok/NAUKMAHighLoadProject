import { useCallback, useEffect, useState } from "react";
import {
  EventResponse,
  EventStatus,
  PaginatedEventResponse,
} from "@/components/types/event";

type SortDirection = "asc" | "desc";

interface UsePaginatedEventsOptions {
  initialPage?: number;
  size?: number;
  sortBy?: string;
  direction?: SortDirection;
  statuses?: EventStatus | EventStatus[];
}

interface UsePaginatedEventsReturn {
  events: EventResponse[];
  page: number;
  setPage: (page: number) => void;
  totalPages: number;
  totalElements: number;
  loading: boolean;
  error: string | null;
  loadNextPage: () => void;
  loadPrevPage: () => void;
  isFirstPage: boolean;
  isLastPage: boolean;
  refresh: () => Promise<void>;
  setPageSize: (size: number) => void;
  setStatusFilter: (statuses: EventStatus | EventStatus[] | null) => void;
  setSortOptions: (sortBy: string, direction: SortDirection) => void;
}

export const usePaginatedEvents = ({
  initialPage = 0,
  size = 10,
  sortBy = "createdAt",
  direction = "desc",
  statuses,
}: UsePaginatedEventsOptions = {}): UsePaginatedEventsReturn => {
  const [events, setEvents] = useState<EventResponse[]>([]);
  const [page, setPage] = useState<number>(initialPage);
  const [pageSize, setPageSize] = useState<number>(size);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [sortField, setSortField] = useState<string>(sortBy);
  const [sortDirection, setSortDirection] = useState<SortDirection>(direction);
  const [statusFilter, setStatusFilter] = useState<EventStatus | EventStatus[] | null>(
    statuses || null
  );

  const fetchEvents = useCallback(async () => {
    setLoading(true);
    setError(null);

    const params = new URLSearchParams({
      page: page.toString(),
      size: pageSize.toString(),
      sortBy: sortField,
      direction: sortDirection,
    });

    if (statusFilter) {
      const statusArray = Array.isArray(statusFilter) ? statusFilter : [statusFilter];
      statusArray.forEach((status) => params.append("statuses", status));
    }

    try {
      const res = await fetch(`/api/events?${params.toString()}`);
      if (!res.ok) {
        throw new Error("Failed to fetch events");
      }
      const data: PaginatedEventResponse = await res.json();
      setEvents(data.content);
      setTotalPages(data.totalPages);
      setTotalElements(data.totalElements);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unknown error");
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, sortField, sortDirection, statusFilter]);

  useEffect(() => {
    fetchEvents();
  }, [fetchEvents]);

  const loadNextPage = useCallback(() => {
    if (page < totalPages - 1) {
      setPage(page + 1);
    }
  }, [page, totalPages]);

  const loadPrevPage = useCallback(() => {
    if (page > 0) {
      setPage(page - 1);
    }
  }, [page]);

  const setPageSizeWithReset = useCallback((newSize: number) => {
    setPageSize(newSize);
    setPage(0); // Reset to first page when changing page size
  }, []);

  const setStatusFilterWithReset = useCallback((newStatus: EventStatus | EventStatus[] | null) => {
    setStatusFilter(newStatus);
    setPage(0); // Reset to first page when changing filters
  }, []);

  const setSortOptions = useCallback((newSortField: string, newSortDirection: SortDirection) => {
    setSortField(newSortField);
    setSortDirection(newSortDirection);
    setPage(0); // Reset to first page when changing sort
  }, []);

  return {
    events,
    page,
    setPage,
    totalPages,
    totalElements,
    loading,
    error,
    loadNextPage,
    loadPrevPage,
    isFirstPage: page === 0,
    isLastPage: page >= totalPages - 1,
    refresh: fetchEvents,
    setPageSize: setPageSizeWithReset,
    setStatusFilter: setStatusFilterWithReset,
    setSortOptions,
  };
};