import type { NextApiRequest, NextApiResponse } from "next";

export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  try {
    const headers: HeadersInit = {
      "Content-Type": "application/json"
    };

    const authHeader = req.headers.authorization;
    if (authHeader) {
      headers["Authorization"] = authHeader;
    }
    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/api/v1/events`,
        {
          method: "POST",
          headers,
          body: req.body
        }
    );

    if (!response.ok) {
      const errorText = await response.text();

      let errorMessage = `Failed to create event: ${response.status}`;
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

    res.status(200).json(await response.json());
  } catch (error: unknown) {
    const errorMessage =
        error instanceof Error ? error.message : "Internal server error";
    console.error("API error:", errorMessage, process.env.NEXT_PUBLIC_API_URL);
    res.status(500).json({ error: errorMessage, status: 500 });
  }
}
