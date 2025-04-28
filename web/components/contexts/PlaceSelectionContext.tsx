import React, { createContext, useContext, ReactNode } from 'react';
import { usePlaceSelection } from '@/components/hooks/usePlaceSelection';
import { PlaceSelectionContextType } from '@/components/types/place';

const PlaceSelectionContext = createContext<PlaceSelectionContextType | undefined>(undefined);

interface PlaceSelectionProviderProps {
  children: ReactNode;
  eventId: string;
}

export const PlaceSelectionProvider: React.FC<PlaceSelectionProviderProps> = ({ 
  children, 
  eventId 
}) => {
  const selectionData = usePlaceSelection(eventId);
  
  return (
    <PlaceSelectionContext.Provider value={selectionData}>
      {children}
    </PlaceSelectionContext.Provider>
  );
};

export const usePlaceSelectionContext = () => {
  const context = useContext(PlaceSelectionContext);
  
  if (context === undefined) {
    throw new Error('usePlaceSelectionContext must be used within a PlaceSelectionProvider');
  }
  
  return context;
};