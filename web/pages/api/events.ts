import type { NextApiRequest, NextApiResponse } from "next";
import queryString from "query-string";
import { PaginatedEventResponse } from "@/components/types/event";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<
    PaginatedEventResponse | { error: string; status?: number }
  >
) {
  const { page, size, sortBy, direction, statuses } = req.query;

  try {
    const queryParams: Record<string, string | string[]> = {
      page: page ?? "0",
      size: size ?? "10",
      sortBy: sortBy ?? "createdAt",
      direction: direction ?? "desc",
    };

    if (statuses) {
      queryParams.statuses = Array.isArray(statuses) ? statuses : [statuses];
    }

    const stringifiedParams = queryString.stringify(queryParams, {
      arrayFormat: "none",
    });

    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };

    const authHeader = req.headers.authorization;
    if (authHeader) {
      headers["Authorization"] = authHeader;
    }

    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/v1/events?${stringifiedParams}`,
      {
        method: "GET",
        headers,
      }
    );

    if (response.status == 401) {
      res.status(401).json({
        error: "Unauthorized",
        status: 401,
      });
    }


    if (!response.ok) {
      const errorText = await response.text();

      let errorMessage = `Failed to fetch events: ${response.status}`;
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

    const data: PaginatedEventResponse = await response.json();
    res.status(200).json(data);
  } catch (error: unknown) {
    const errorMessage =
      error instanceof Error ? error.message : "Internal server error";
    console.error("API error:", errorMessage, process.env.NEXT_PUBLIC_API_URL);
    res.status(500).json({ error: errorMessage, status: 500 });
  }
}
