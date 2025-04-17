import NextLink from "next/link";
import { Anchor, Button } from '@mantine/core';
import { FC, ReactNode } from "react";

interface Props {
  href: string;
  children: ReactNode;
}

const Link:FC<Props> = ({href, children}) => {
  return (
      <Anchor<typeof NextLink> href={href} component={NextLink}>
        <Button variant="light">
          {children}
        </Button>
      </Anchor>
  );
};

export default Link;