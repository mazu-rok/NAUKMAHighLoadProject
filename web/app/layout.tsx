import AppShell from "@/components/common/AppShell/AppShell";
import type { Metadata } from "next";
import "./globals.css";
import '@mantine/core/styles.css';
import '@mantine/carousel/styles.css';
import '@mantine/dates/styles.css';

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
        <AppShell>
          {children}
        </AppShell>
      </MantineProvider>
      </body>
    </html>
  );
}
