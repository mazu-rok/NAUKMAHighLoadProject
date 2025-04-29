// pages/api/buckets/[userId].ts
import type { NextApiRequest, NextApiResponse } from "next";
import { BucketResponse, BucketTicketDto } from "@/components/types/bucket";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<BucketResponse | { error?: string; status?: number }>
) {
  const { userId } = req.query;
  const accessToken = req.headers.authorization?.split(" ")[1];

  if (!userId || typeof userId !== "string") {
    return res
      .status(400)
      .json({ error: "Invalid userId parameter", status: 400 });
  }

  const apiUrl = `${process.env.NEXT_PUBLIC_API_URL}/api/v1/buckets/${userId}`;
  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  }

  try {
    switch (req.method) {
      case "GET":
        const getResponse = await fetch(apiUrl, {
          method: "GET",
          headers,
        });
        if (!getResponse.ok) {
          const error = await getResponse.json();
          return res.status(getResponse.status).json({ error: error.message, status: getResponse.status });
        }
        const bucketData: BucketResponse = await getResponse.json();
        return res.status(200).json(bucketData as BucketResponse);

      case "POST": // For adding a ticket
        const postResponse = await fetch(apiUrl, {
          method: "POST",
          headers,
          body: JSON.stringify(req.body as BucketTicketDto),
        });
        if (!postResponse.ok) {
          const error = await postResponse.json();
          return res.status(postResponse.status).json({ error: error.message, status: postResponse.status });
        }
        return res.status(204).end();

      case "DELETE": // For removing a ticket
        const deleteResponse = await fetch(apiUrl, {
          method: "DELETE",
          headers,
          body: JSON.stringify(req.body as BucketTicketDto),
        });
        if (!deleteResponse.ok) {
          const error = await deleteResponse.json();
          return res.status(deleteResponse.status).json({ error: error.message, status: deleteResponse.status });
        }
        return res.status(204).end();

      default:
        res.setHeader("Allow", ["GET", "POST", "DELETE"]);
        return res.status(405).end(`Method ${req.method} Not Allowed`);
    }
  } catch (error: unknown) {
    const errorMessage =
      error instanceof Error ? error.message : "Internal server error";
    console.error("API error:", errorMessage);
    res.status(500).json({ error: errorMessage, status: 500 });
  }
}