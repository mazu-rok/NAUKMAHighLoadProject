import React from 'react';
import { Place } from '@/components/types/place';
import { usePlaceSelectionContext } from '@/components/contexts/PlaceSelectionContext';
import { Box, Tooltip } from '@mantine/core';

interface PlaceItemProps {
  place: Place;
}

export const PlaceItem: React.FC<PlaceItemProps> = ({ place }) => {
  const { togglePlaceSelection, utils } = usePlaceSelectionContext();
  const isSelected = utils.isSelected(place.placeId);
  const canSelect = utils.canSelect(place);
  
  const getStatusColor = () => {
    if (isSelected) return 'blue';
    
    switch (place.status) {
      case 'AVAILABLE':
        return 'green';
      case 'BOOKED':
        return 'red';
      case 'ORDERED':
        return 'gray';
      default:
        return 'gray';
    }
  };
  
  return (
    <Tooltip label={`Row ${place.row}, Place ${place.place}`}>
      <Box
        component="button"
        w={32}
        h={32}
        m={4}
        display="flex"
        style={{
          alignItems: 'center',
          justifyContent: 'center',
          borderRadius: '4px',
          fontSize: '12px',
          fontWeight: 500,
          cursor: canSelect ? 'pointer' : 'not-allowed',
          backgroundColor: `var(--mantine-color-${getStatusColor()}-${isSelected ? 6 : 1})`,
          color: isSelected ? 'white' : `var(--mantine-color-${getStatusColor()}-${place.status === 'AVAILABLE' ? 9 : 6})`,
          border: 'none',
          transition: 'all 0.2s'
        }}
        onClick={() => canSelect && togglePlaceSelection(place)}
        disabled={!canSelect}
      >
        {place.place}
      </Box>
    </Tooltip>
  );
};