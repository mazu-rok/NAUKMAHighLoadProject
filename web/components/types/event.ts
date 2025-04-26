export type EventStatus = "DRAFT" | "SCHEDULED" | "ENDED";

export interface ImageMetadata {
    url: string;
    image_id: string;
}

export interface EventResponse {
  eventId: string;
  authorId: string;
  title: string;
  description: string | null;
  status: EventStatus;
  startTime: string;
  endTime: string;
  locationAddress: string;
  createdAt: string;
  updatedAt: string;
  imagesMetadata: ImageMetadata[];
}

export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface PaginatedEventResponse {
  content: EventResponse[];
  pageable: Pageable;
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: Sort;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
