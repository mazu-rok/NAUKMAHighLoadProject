import Header from "@/components/common/Header/Header";
import React, { FC, ReactNode } from "react";
import { AppShell as AppShellMantine, AppShellHeader, AppShellMain, Box } from "@mantine/core";

interface Props {
  children: ReactNode;
}

const HEADER_HEIGHT = 170;

const AppShell:FC<Props> = ({children}) => {
  return (
      <AppShellMantine className="bg-orange">
        <AppShellHeader p='md' h={HEADER_HEIGHT} className="bg-orange" style={{borderBottom: 0}}>
          <Header/>
        </AppShellHeader>

        <AppShellMain mt={HEADER_HEIGHT} mih={`calc(100vh - ${HEADER_HEIGHT}px)`}>
            <Box h={`calc(100vh - ${HEADER_HEIGHT}px)`}>
                {children}
            </Box>
        </AppShellMain>
      </AppShellMantine>
  );
};
export {HEADER_HEIGHT}
export default AppShell;