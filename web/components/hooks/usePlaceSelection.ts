import { useState, useEffect, useCallback } from 'react';
import { Place, EventPlaceLayout, EventPlaceUtils } from '@/components/types/place';

export const usePlaceSelection = (eventId: string) => {
  const [layout, setLayout] = useState<EventPlaceLayout | null>(null);
  const [selectedPlaces, setSelectedPlaces] = useState<Place[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

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
    fetchPlaces();
  }, [fetchPlaces]);
  
  const togglePlaceSelection = useCallback((place: Place) => {
    if (place.status !== 'AVAILABLE') return;
    
    setSelectedPlaces(prev => {
      const isAlreadySelected = prev.some(p => p.placeId === place.placeId);
      
      if (isAlreadySelected) {
        return prev.filter(p => p.placeId !== place.placeId);
      } else {
        return [...prev, {...place, status: 'SELECTED'}];
      }
    });
  }, []);
  
  const clearSelection = useCallback(() => {
    setSelectedPlaces([]);
  }, []);
  
  const confirmSelection = useCallback(async () => {
    // Implement API call to book/reserve selected places
    if (selectedPlaces.length === 0) return Promise.resolve();
    
    try {
      const headers: Record<string, string> = {
        'Content-Type': 'application/json'
      };
      
      if (localStorage.getItem('accessToken') != null) {
        headers['Authorization'] = `Bearer ${localStorage.getItem('accessToken')}`;
      }
      
      // TODO: Implement the API call to book/reserve selected places
      
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
      return place.status === 'AVAILABLE';
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