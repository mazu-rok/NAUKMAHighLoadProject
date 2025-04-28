// components/common/places/SelectionSummary.tsx
import React, { useState } from 'react';
import { usePlaceSelectionContext } from '@/components/contexts/PlaceSelectionContext';
import { Box, Button, Divider, Group, Text, Alert } from '@mantine/core';
import { IconAlertCircle, IconCheck } from '@tabler/icons-react';

export const SelectionSummary: React.FC = () => {
  const { selectedPlaces, clearSelection, confirmSelection } = usePlaceSelectionContext();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [bookingError, setBookingError] = useState<string | null>(null);
  const [bookingSuccess, setBookingSuccess] = useState(false);
  
  // Reset success message when new places are selected
  React.useEffect(() => {
    if (selectedPlaces.length > 0 && bookingSuccess) {
      setBookingSuccess(false);
    }
  }, [selectedPlaces, bookingSuccess]);
  
  const handleConfirm = async () => {
    try {
      setIsSubmitting(true);
      setBookingError(null);
      await confirmSelection();
      setBookingSuccess(true);
      clearSelection();
    } catch (error) {
      console.error('Failed to confirm selection:', error);
      setBookingError(typeof error === 'string' ? error : 'Failed to book places. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };
  
  const handleNewBooking = () => {
    setBookingSuccess(false);
  };
  
  // Show empty state if no places selected and no success message
  if (selectedPlaces.length === 0 && !bookingSuccess) {
    return (
      <Box py="md" ta="center">
        <Text c="dimmed">No places selected</Text>
      </Box>
    );
  }
  
  // Show success message with option to book more tickets
  if (bookingSuccess) {
    return (
      <Box mt="md" pt="md">
        <Divider mb="md" />
        <Alert 
          icon={<IconCheck size={16} />} 
          title="Booking Successful!" 
          color="green"
          my="md"
          withCloseButton
          onClose={handleNewBooking}
        >
          <Text mb="md">Your seats have been successfully booked.</Text>
          <Button 
            variant="outline" 
            color="green" 
            size="sm"
            onClick={handleNewBooking}
          >
            Book More Tickets
          </Button>
        </Alert>
      </Box>
    );
  }
  
  // Show selection summary and booking controls
  return (
    <Box mt="md" pt="md">
      
      {bookingError && (
        <Alert 
          icon={<IconAlertCircle size={16} />} 
          title="Booking Error" 
          color="red"
          mb="md"
          withCloseButton
          onClose={() => setBookingError(null)}
        >
          {bookingError}
        </Alert>
      )}
      
      <Group justify="flex-end">
        <Button 
          variant="outline" 
          color="gray" 
          onClick={clearSelection}
          disabled={isSubmitting}
        >
          Clear
        </Button>
        <Button 
          color="blue" 
          onClick={handleConfirm}
          loading={isSubmitting}
        >
          Confirm Selection
        </Button>
      </Group>
    </Box>
  );
};