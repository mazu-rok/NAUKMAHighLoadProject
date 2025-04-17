'use client'
import { Badge, Button, Card, Group, Image, Text } from "@mantine/core";
import { useRouter } from "next/navigation";

export default function EventsPage() {
  const router = useRouter()

  return (
      <Group p="md">
        <Card shadow="sm" padding="lg" radius="md" withBorder w='300px'>
          <Card.Section>
            <Image
                src="https://raw.githubusercontent.com/mantinedev/mantine/master/.demo/images/bg-8.png"
                height={160}
                alt="Norway"
            />
          </Card.Section>

          <Group justify="space-between" mt="md" mb="xs">
            <Text fw={500}>Norway Fjord Adventures</Text>
            <Badge color="pink">On Sale</Badge>
          </Group>

          <Text size="sm" c="dimmed">
            With Fjord Tours you can explore more of the magical fjord landscapes with tours and
            activities on and around the fjords of Norway
          </Text>

          <Button color="blue" fullWidth mt="md" radius="md" onClick={() => router.push('/events/' + 1)}>
            Go to details
          </Button>
        </Card>
        <Card shadow="sm" padding="lg" radius="md" withBorder w='300px' onClick={() => router.push('/events/' + 2)}>
          <Card.Section>
            <Image
                src="https://raw.githubusercontent.com/mantinedev/mantine/master/.demo/images/bg-8.png"
                height={160}
                alt="Norway"
            />
          </Card.Section>

          <Group justify="space-between" mt="md" mb="xs">
            <Text fw={500}>Norway Fjord Adventures</Text>
            <Badge color="pink">On Sale</Badge>
          </Group>

          <Text size="sm" c="dimmed">
            With Fjord Tours you can explore more of the magical fjord landscapes with tours and
            activities on and around the fjords of Norway
          </Text>

          <Button color="blue" fullWidth mt="md" radius="md">
            Go to details
          </Button>
        </Card>
      </Group>
  );
}
