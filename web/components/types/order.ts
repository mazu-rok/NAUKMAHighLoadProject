export interface OrderedTicket {
  placeId: string;
  row: number;
  place: number;
}

export interface Order {
  id: string;
  userId: string;
  eventId: string;
  eventName: string;
  tickets: OrderedTicket[];
  createdAt: string;
}
