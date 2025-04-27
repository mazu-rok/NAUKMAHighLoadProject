import type { NextApiRequest, NextApiResponse } from "next";

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<[{orderId: string, eventName: string, date: string}]| { error: string; status?: number }
  >
) {
  res.status(200).json([{orderId: "1", eventName: "test1", date: "2022-01-01"}]);
}
