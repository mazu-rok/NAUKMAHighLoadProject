'use client'
import Bucket from "@/components/Bucket";
import Link from "@/components/UI/Link/Link";
import { useDisclosure } from "@mantine/hooks";
import React from "react";
import { Box, Group } from "@mantine/core";
import { IconBucket } from "@tabler/icons-react";

const Header = () => {
  const [opened, { open, close }] = useDisclosure(false);
  return (
      <Group>
        <Link href="/events">Events</Link>

        <Link href="/profile">Profile</Link>

        <Link href="/dashboard">Create event</Link>

        <Link href="/sign-in">Sign in</Link>

        <Link href="/sign-up">Sign up</Link>

        <Box style={{cursor: "pointer", marginLeft: "auto"}} onClick={open}>
          <IconBucket color="white"/>
        </Box>
        <Bucket opened={opened} close={close}/>
      </Group>
  );
};

export default Header;