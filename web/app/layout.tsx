import type { Metadata } from "next";
import "./globals.css";
import '@mantine/core/styles.css';

import { createTheme, MantineProvider } from '@mantine/core';

const theme = createTheme({});

export const metadata: Metadata = {
  title: "КвиточОК",
  description: "Купуй квитки на найкращі події в Україні",
}; 

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
      <MantineProvider theme={theme}>
          {children}
      </MantineProvider>
      </body>
    </html>
  );
}
