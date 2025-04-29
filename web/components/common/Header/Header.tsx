import Link from "@/components/UI/Link/Link";
import React from "react";
import {Group} from "@mantine/core"

const Header = () => {
  return (
      <Group>
        <Link href="/events">Events</Link>

        <Link href="/profile">Profile</Link>

        <Link href="/dashboard">Dashboard</Link>

        <Link href="/sign-in">Sign in</Link>

        <Link href="/sign-up">Sign up</Link>
      </Group>
  );
};

export default Header;