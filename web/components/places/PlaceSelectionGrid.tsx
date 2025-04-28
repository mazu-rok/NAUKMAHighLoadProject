import React from 'react';
import { RowDisplay } from './RowDisplay';
import { usePlaceSelectionContext } from '@/components/contexts/PlaceSelectionContext';
import { Box, Center, Flex, Loader, Paper, Text } from '@mantine/core';

export const PlaceSelectionGrid: React.FC = () => {
  const { layout, loading, error } = usePlaceSelectionContext();
  
  if (loading) {
    return (
      <Center py="xl">
        <Loader />
      </Center>
    );
  }
  
  if (error) {
    return (
      <Center py="xl">
        <Text c="red">{error}</Text>
      </Center>
    );
  }
  
  if (!layout) {
    return (
      <Center py="xl">
        <Text>No seating layout available</Text>
      </Center>
    );
  }
  
  return (
    <Box my="md">
      <Paper 
        bg="gray.1" 
        withBorder={false} 
        ta="center" 
        p="md" 
        mb="xl" 
        radius="md"
      >
        <Text fw={500}>Screen / Stage</Text>
      </Paper>
      
      <Box display={'flex'} style={{ flexDirection: 'column', alignItems: 'center' }}>
        {Array.from({ length: layout.rows }, (_, i) => i + 1).map(rowNumber => (
          <RowDisplay key={`row-${rowNumber}`} rowNumber={rowNumber} />
        ))}
      </Box>
      
      <Flex justify="center" mt="xl" gap="md">
        <Flex align="center">
          <Box 
            w={16} 
            h={16} 
            mr="xs" 
            style={{ 
              backgroundColor: 'var(--mantine-color-green-1)', 
              borderRadius: '4px' 
            }} 
          />
          <Text size="sm">Available</Text>
        </Flex>
        <Flex align="center">
          <Box 
            w={16} 
            h={16} 
            mr="xs" 
            style={{ 
              backgroundColor: 'var(--mantine-color-blue-6)', 
              borderRadius: '4px' 
            }} 
          />
          <Text size="sm">Selected</Text>
        </Flex>
        <Flex align="center">
          <Box 
            w={16} 
            h={16} 
            mr="xs" 
            style={{ 
              backgroundColor: 'var(--mantine-color-gray-6)', 
              borderRadius: '4px' 
            }} 
          />
          <Text size="sm">Booked</Text>
        </Flex>
        <Flex align="center">
          <Box 
            w={16} 
            h={16} 
            mr="xs" 
            style={{ 
              backgroundColor: 'var(--mantine-color-red-6)', 
              borderRadius: '4px' 
            }} 
          />
          <Text size="sm">Ordered</Text>
        </Flex>
      </Flex>
    </Box>
  );
};
