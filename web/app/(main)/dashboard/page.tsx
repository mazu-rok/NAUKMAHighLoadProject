"use client";


import { EventStatus } from "@/components/types/event";
import { Button, NumberInput, Paper, Select, Stack, TextInput, Title } from "@mantine/core";
import { DateTimePicker } from "@mantine/dates";
import { useForm } from "@mantine/form";
import {useRouter} from 'next/navigation'

export default function DashboardPage() {
  const form = useForm({
    mode: "uncontrolled",
    initialValues: {
      title: "",
      description: "",
      status: "SCHEDULED" as EventStatus,
      startTime: new Date(),
      endTime: new Date(),
      locationAddress: "",
      rows: 4,
      places: 4
    },

    validate: {}
  });
  const router = useRouter()

  const onSubmit = async (values: { endTime: Date; startTime: Date }) => {
    const endTime = values.endTime.toISOString() as string;
    const startTime = values.startTime.toISOString() as string;
    try {
      const headers: Record<string, string> = {};
      if (localStorage.getItem("accessToken") != null) {
        headers["Authorization"] = `Bearer ${localStorage.getItem("accessToken")}`;
      }
      const res = await fetch(`/api/event/create`, {
        method: "POST",
        body: JSON.stringify({ ...values, endTime, startTime }),
        headers
      });
      if (res.ok) {
        console.log("Event created:", await res.json());
        router.push('/events');
      } else {
        console.error("Failed to create event:", await res.json());
      }

    } catch (error) {
      console.error('Event create error:', error);
    }
  }

  return <Paper p="xl" style={{
    width: "100%",
    height: "auto",
    minHeight: "100px",
    borderRadius: "80px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center"
  }}>
    <form onSubmit={form.onSubmit(onSubmit)}>
      <Stack w={400}>
        <Title order={1}>Create Event</Title>

        <TextInput
            label="Title"
            placeholder="Title"
            key={form.key("title")}
            {...form.getInputProps("title")}
        />
        <TextInput
            label="Description"
            placeholder="Description"
            key={form.key("description")}
            {...form.getInputProps("description")}
        />
        <TextInput
            label="Address"
            placeholder="Address"
            key={form.key("locationAddress")}
            {...form.getInputProps("locationAddress")}
        />
        <Select
            label="Select status"
            placeholder="Select status"
            data={["DRAFT", "SCHEDULED", "ENDED"]}
            key={form.key("status")}
            {...form.getInputProps("status")}
        />
        <DateTimePicker
            label="Start Time"
            placeholder="Start Time"
            key={form.key("startTime")}
            {...form.getInputProps("startTime")}
        />
        <DateTimePicker
            label="End Time"
            placeholder="End Time"
            key={form.key("endTime")}
            {...form.getInputProps("endTime")}
        />
        <NumberInput
            label="Rows"
            placeholder="Rows"
            key={form.key("rows")}
            {...form.getInputProps("rows")}
        />
        <NumberInput
            label="Places"
            placeholder="Places"
            key={form.key("places")}
            {...form.getInputProps("places")}
        />
        <Button type="submit">Submit</Button>
      </Stack>
    </form>
  </Paper>;
}
