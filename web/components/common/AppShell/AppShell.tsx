import Header from "@/components/common/Header/Header";
import React, { FC, ReactNode } from "react";
import {AppShell as AppShellMantine, AppShellHeader, AppShellMain} from "@mantine/core"

interface Props {
  children: ReactNode;
}

const HEADER_HEIGHT = 70;

const AppShell:FC<Props> = ({children}) => {
  return (
      <AppShellMantine>
        <AppShellHeader p='md' h={HEADER_HEIGHT}>
          <Header/>
        </AppShellHeader>

        <AppShellMain mt={HEADER_HEIGHT}>
          {children}
        </AppShellMain>
      </AppShellMantine>
  );
};

export default AppShell;