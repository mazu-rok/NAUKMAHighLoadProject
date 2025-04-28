import type { NextApiRequest, NextApiResponse } from "next";
import { Place } from "@/components/types/place";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<Place[] | { error: string; status?: number }>
) {
  const { eventId } = req.query;

  if (!eventId) {
    return res.status(400).json({ error: "Event ID is required", status: 400 });
  }

  try {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };

    const authHeader = req.headers.authorization;
    if (authHeader) {
      headers["Authorization"] = authHeader;
    }

    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/v1/events/places?eventId=${eventId}`,
      {
        method: "GET",
        headers,
      }
    );

    if (!response.ok) {
      const errorText = await response.text();

      let errorMessage = `Failed to fetch places: ${response.status}`;
      try {
        const errorJson = JSON.parse(errorText);
        if (errorJson.message) {
          errorMessage = `Error: ${errorJson.message}`;
        }
      } catch {
        errorMessage = `Error: ${errorText}`;
      }

      throw new Error(errorMessage);
    }

    const data: Place[] = await response.json();
    res.status(200).json(data);
  } catch (error: unknown) {
    const errorMessage =
      error instanceof Error ? error.message : "Internal server error";
    console.error("API error:", errorMessage);
    res.status(500).json({ error: errorMessage, status: 500 });
  }
}