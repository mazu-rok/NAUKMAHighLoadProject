export interface BucketTicketDto {
  eventId: string;
  placeId: string;
}

export interface BucketResponse {
  userId: string;
  tickets: BucketTicketDto[];
}
