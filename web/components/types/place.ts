export type PlaceStatus = 'AVAILABLE' | 'SELECTED' | 'BOOKED' | 'ORDERED';

export interface Place {
  placeId: string;
  eventId: string;
  row: number;
  place: number;
  status: PlaceStatus;
}

export interface PlacesSelectionState {
  selectedPlaces: Place[];
}

export interface EventPlaceLayout {
  rows: number;
  placesPerRow: number;
  places: Place[];
}

export interface EventPlaceUtils {
  placesInRow: (row: number) => Place[];
  isSelected: (placeId: string) => boolean;
  canSelect: (place: Place) => boolean;
}

export interface PlaceSelectionContextType {
  eventId: string;
  layout: EventPlaceLayout | null;
  selectedPlaces: Place[];
  loading: boolean;
  error: string | null;
  togglePlaceSelection: (place: Place) => void;
  clearSelection: () => void;
  confirmSelection: () => Promise<void>;
  utils: EventPlaceUtils;
}
