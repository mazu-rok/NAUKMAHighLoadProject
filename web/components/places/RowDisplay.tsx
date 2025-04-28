import React from 'react';
import { PlaceItem } from './PlaceItem';
import { usePlaceSelectionContext } from '@/components/contexts/PlaceSelectionContext';
import { Box, Flex, Text } from '@mantine/core';

interface RowDisplayProps {
  rowNumber: number;
}

export const RowDisplay: React.FC<RowDisplayProps> = ({ rowNumber }) => {
  const { utils } = usePlaceSelectionContext();
  const places = utils.placesInRow(rowNumber);
  
  return (
    <Flex align="center" mb="xs">
      <Box w={32} ta="center" fw={500} mr="md">
        <Text>{rowNumber}</Text>
      </Box>
      <Flex wrap="wrap">
        {places.map(place => (
          <PlaceItem key={place.placeId} place={place} />
        ))}
      </Flex>
    </Flex>
  );
};
