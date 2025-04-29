import { useState, useEffect, useCallback } from 'react';
import { Place, EventPlaceLayout, EventPlaceUtils } from '@/components/types/place';
import { WS_URL } from '@/components/config/config';

export const usePlaceSelection = (eventId: string) => {
  const [layout, setLayout] = useState<EventPlaceLayout | null>(null);
  const [selectedPlaces, setSelectedPlaces] = useState<Place[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const userId = localStorage.getItem('userId');
  const fetchPlaces = useCallback(async () => {
    if (!eventId) return;

    setLoading(true);
    setError(null);

    try {
      const headers: Record<string, string> = {};
      if (localStorage.getItem('accessToken') != null) {
        headers['Authorization'] = `Bearer ${localStorage.getItem('accessToken')}`;
      }

      const response = await fetch(`/api/places?eventId=${eventId}`, {
        headers
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch places: ${response.status}`);
      }

      const places: Place[] = await response.json();

      const maxRow = Math.max(...places.map(p => p.row));
      const placesPerRow = places.filter(p => p.row === 1).length;

      setLayout({
        rows: maxRow,
        placesPerRow,
        places,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to load places');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [eventId]);

  useEffect(() => {
    if (!eventId) return;

    const ws = new WebSocket(`${WS_URL}/ws/v1/places/events?eventId=${eventId}`);

    ws.onopen = () => {
      console.log('Connected to WebSocket');
    };

    ws.onmessage = (event) => {
      try {
        const message = JSON.parse(event.data);
        if (message && message.placeId && message.status) {
          setLayout(prevLayout => {
            if (prevLayout?.places) {
              const updatedPlaces = prevLayout.places.map(place =>
                place.placeId === message.placeId ? { ...place, status: message.status } : place
              );
              return { ...prevLayout, places: updatedPlaces };
            }
            return prevLayout;
          });

          setSelectedPlaces(prevSelected =>
            prevSelected.map(selectedPlace =>
              selectedPlace.placeId === message.placeId ? { ...selectedPlace, status: message.status } : selectedPlace
            )
          );
        }
      } catch (error) {
        console.error('Failed to parse WebSocket message:', error);
      }
    };

    ws.onclose = () => {
      console.log('WebSocket connection closed');
    };

    ws.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    return () => {
      ws.close();
    };
  }, [eventId, setLayout, setSelectedPlaces]);

  useEffect(() => {
    fetchPlaces();
  }, [fetchPlaces]);

  const togglePlaceSelection = useCallback((place: Place) => {
    if (place.status !== 'AVAILABLE' && !selectedPlaces.some(p => p.placeId === place.placeId)) return;

    setSelectedPlaces(prev => {
      const isAlreadySelected = prev.some(p => p.placeId === place.placeId);

      if (isAlreadySelected) {
        return prev.filter(p => p.placeId !== place.placeId);
      } else {
        return [...prev, { ...place, status: 'SELECTED' }];
      }
    });
  }, [selectedPlaces]);

  const clearSelection = useCallback(() => {
    setSelectedPlaces([]);
  }, []);

  const confirmSelection = useCallback(async () => {
    if (selectedPlaces.length === 0) return Promise.resolve();

    try {
      const headers: Record<string, string> = {
        'Content-Type': 'application/json'
      };

      if (localStorage.getItem('accessToken') != null) {
        headers['Authorization'] = `Bearer ${localStorage.getItem('accessToken')}`;
      }

      // TODO: Implement the API call to book/reserve selected places
        const response = await fetch(`/api/buckets/${userId}`, {
        method: 'POST',
        headers,
        body: JSON.stringify({ placeId: selectedPlaces[0].placeId, eventId })
      });
      
      if (!response.ok) {
        throw new Error('Failed to book places');
      }
      console.log('Confirming selection:', selectedPlaces);
      return Promise.resolve();
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Failed to book places';
      console.error(errorMessage);
      return Promise.reject(errorMessage);
    }
  }, [selectedPlaces]);

  const utils: EventPlaceUtils = {
    placesInRow: (row: number) => {
      return layout?.places.filter(p => p.row === row).sort((a, b) => a.place - b.place) || [];
    },
    isSelected: (placeId: string) => {
      return selectedPlaces.some(p => p.placeId === placeId);
    },
    canSelect: (place: Place) => {
      return place.status === 'AVAILABLE' || selectedPlaces.some(p => p.placeId === place.placeId);
    }
  };

  return {
    eventId,
    layout,
    selectedPlaces,
    loading,
    error,
    togglePlaceSelection,
    clearSelection,
    confirmSelection,
    utils
  };
};