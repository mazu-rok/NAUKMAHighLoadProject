export interface BucketTicketDto {
  eventId: string;
  placeId: string;
}
export interface BucketTicketsDto {
  eventId: string;
  placeIds: string[];
}

export interface BucketResponse {
  userId: string;
  tickets: BucketTicketDto[];
}
