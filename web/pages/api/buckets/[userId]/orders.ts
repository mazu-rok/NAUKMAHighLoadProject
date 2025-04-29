// pages/api/buckets/[userId]/orders.ts
import type { NextApiRequest, NextApiResponse } from "next";
import { Order } from "@/components/types/order";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<Order | Order[] | { error?: string; status?: number }>
) {
  const { userId } = req.query;
  const accessToken = req.headers.authorization?.split(" ")[1];

  if (!userId || typeof userId !== "string") {
    return res
      .status(400)
      .json({ error: "Invalid userId parameter", status: 400 });
  }

  const apiUrl = `${process.env.NEXT_PUBLIC_API_URL}/api/v1/buckets/${userId}/orders`;
  const headers: HeadersInit = {
    "Content-Type": "application/json",
  };

  if (accessToken) {
    headers["Authorization"] = `Bearer ${accessToken}`;
  }

  try {
    switch (req.method) {
      case "POST": // For buying tickets (placing an order)
        const postResponse = await fetch(apiUrl, {
          method: "POST",
          headers,
        });
        if (!postResponse.ok) {
          const error = await postResponse.json();
          return res
            .status(postResponse.status)
            .json({ error: error.message, status: postResponse.status });
        }
        const orderData: Order = await postResponse.json();
        return res.status(200).json(orderData);

      case "GET": // For getting user's orders
        const getResponse = await fetch(apiUrl, {
          method: "GET",
          headers,
        });
        if (!getResponse.ok) {
          const error = await getResponse.json();
          return res
            .status(getResponse.status)
            .json({ error: error.message, status: getResponse.status });
        }
        const ordersData: Order[] = await getResponse.json();
        return res.status(200).json(ordersData);

      default:
        res.setHeader("Allow", ["GET", "POST"]);
        return res.status(405).end(`Method ${req.method} Not Allowed`);
    }
  } catch (error: unknown) {
    const errorMessage =
      error instanceof Error ? error.message : "Internal server error";
    console.error("API error:", errorMessage);
    res.status(500).json({ error: errorMessage, status: 500 });
  }
}
