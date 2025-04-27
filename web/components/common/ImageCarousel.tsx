// components/common/ImageCarousel.tsx
"use client";

import { useState } from "react";
import { Box, Image, Paper, Text, rem } from "@mantine/core";
import { Carousel, Embla, useAnimationOffsetEffect } from '@mantine/carousel';
import { IconChevronLeft, IconChevronRight } from "@tabler/icons-react";

interface ImageMetadata {
  url: string;
  altText?: string;
}

interface ImageCarouselProps {
  images: ImageMetadata[];
  height?: number;
}

export default function ImageCarousel({ images, height = 300 }: ImageCarouselProps) {
  // Set up animation offset for smoother transitions
  const [embla, setEmbla] = useState<Embla | null>(null);
  useAnimationOffsetEffect(embla, 200);

  if (!images || images.length === 0) {
    return (
      <Paper
        h={height}
        bg="gray.1"
        radius="md"
        withBorder
        ta="center"
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Text c="dimmed">No images available</Text>
      </Paper>
    );
  }

  return (
    <Box style={{ position: 'relative', overflow: 'hidden' }} mb="md">
      <Carousel
        getEmblaApi={setEmbla}
        withIndicators
        height={height}
        slideSize="100%"
        slideGap={0}
        loop
        align="center"
        controlsOffset="xs"
        nextControlIcon={<IconChevronRight size={20} />}
        previousControlIcon={<IconChevronLeft size={20} />}
        styles={{
          root: {
            width: '100%',
          },
          control: {
            backgroundColor: 'rgba(255, 255, 255, 0.7)',
            border: 'none',
            color: '#000',
            '&:hover': {
              backgroundColor: 'rgba(255, 255, 255, 0.9)',
            },
            width: rem(34),
            height: rem(34),
            boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
          },
          indicators: {
            bottom: rem(10),
            gap: rem(8),
          },
          indicator: {
            width: rem(8),
            height: rem(8),
            backgroundColor: 'rgba(255, 255, 255, 0.5)',
            transition: 'width 250ms ease, background-color 250ms ease',
            
            '&[dataActive]': {
              width: rem(24),
              backgroundColor: 'white',
            },
          },
        }}
      >
        {images.map((image, index) => (
          <Carousel.Slide key={index}>
            <Box style={{ position: 'relative', height: '100%' }}>
              <Image
                src={image.url}
                alt={image.altText || `Event image ${index + 1}`}
                height={height}
                fit="cover"
                radius="md"
                style={{ 
                  display: 'block',
                  width: '100%',
                  objectFit: 'cover',
                }}
              />
            </Box>
          </Carousel.Slide>
        ))}
      </Carousel>
    </Box>
  );
}