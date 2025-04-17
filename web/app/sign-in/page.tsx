'use client'
import { Modal, Button, Group, TextInput } from '@mantine/core';
import { useForm } from '@mantine/form';
import { useDisclosure } from "@mantine/hooks";
import { useRouter } from 'next/navigation'


export default function SignInPage() {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: {
      email: '',
      password: '',
      termsOfService: false,
    },

    validate: {
      email: (value:string) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
      password: (value:string) => (value.length > 7 ? null : 'Invalid password'),
    },
  });
  const [opened, { close }] = useDisclosure(true);

  const router = useRouter()

  const onSubmit = () => {
    router.push('/events')
    close();
  }

  return (
    <Modal opened={opened} onClose={close} withCloseButton={false} closeOnClickOutside={false}>
      <form onSubmit={form.onSubmit(onSubmit)}>
        <TextInput
            withAsterisk
            label="Email"
            placeholder="your@email.com"
            key={form.key('email')}
            {...form.getInputProps('email')}
            mb='md'
        />

        <TextInput
            withAsterisk
            label="Passwrd"
            placeholder="password"
            key={form.key('password')}
            {...form.getInputProps('password')}
        />

        <Group justify="flex-end" mt="md">
          <Button type="submit">Sign in</Button>
        </Group>

        <Group justify="center" mt="md">
          <Button onClick={() => router.push('/sign-up')} variant="white">
            Go to register
          </Button>
        </Group>

      </form>
    </Modal>
  );
}
