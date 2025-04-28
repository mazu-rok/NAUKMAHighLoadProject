import React from 'react';
import { PlaceSelectionProvider } from '@/components/contexts/PlaceSelectionContext';
import { PlaceSelectionGrid } from './PlaceSelectionGrid';
import { SelectionSummary } from './SelectionSummary';
import { Paper, Title } from '@mantine/core';

interface PlaceSelectionContainerProps {
  eventId: string;
}

export const PlaceSelectionContainer: React.FC<PlaceSelectionContainerProps> = ({ eventId }) => {
  return (
    <PlaceSelectionProvider eventId={eventId}>
      <Paper p="lg" radius="md" withBorder shadow="sm">
        <Title order={3} mb="lg">Select Your Seats</Title>
        <PlaceSelectionGrid />
        <SelectionSummary />
      </Paper>
    </PlaceSelectionProvider>
  );
};