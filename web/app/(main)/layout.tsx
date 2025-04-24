import AppShell from "@/components/common/AppShell/AppShell";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AppShell >
      {children}
    </AppShell>
  );
}
