export interface OrderedTicket {
  placeId: string;
  row: number;
  place: number;
}

export interface Order {
  userId: string;
  eventId: string;
  eventName: string;
  tickets: OrderedTicket[];
  createdAt: string;
}
