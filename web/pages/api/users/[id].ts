import type { NextApiRequest, NextApiResponse } from "next";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<{username: string; email: string} | { error: string; status?: number }>
) {
  const { id } = req.query;
  console.log("1");
  if (!id || Array.isArray(id)) {
    return res.status(400).json({ error: "Invalid event ID", status: 400 });
  }
  console.log("2");

  try {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };

    const authHeader = req.headers.authorization;
    if (authHeader) {
      headers["Authorization"] = authHeader;
    }
    console.log(id);

    const response = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/api/v1/users/${id}`,
      {
        method: "GET",
        headers,
      }
    );

    if (!response.ok) {
      const errorText = await response.text();

      let errorMessage = `Failed to fetch user: ${response.status}`;
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
    console.error("API error:", errorMessage);
    res.status(500).json({ error: errorMessage, status: 500 });
  }
}
