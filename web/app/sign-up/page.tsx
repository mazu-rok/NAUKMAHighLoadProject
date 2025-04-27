'use client'
import {useForm} from '@mantine/form';
import {useRouter} from 'next/navigation'
import {
  TextInput,
  PasswordInput,
  Button,
  Text,
  Anchor,
  Paper,
  Container,
  Stack,
  Center,
} from "@mantine/core";
import { ApiService } from '@/services/ApiService';


export default function SignUpPage() {
  const form = useForm({
    mode: 'uncontrolled',
    initialValues: {
      username: '',
      email: '',
      password: '',
    },

    validate: {
      email: (value:string) => (/^\S+@\S+$/.test(value) ? null : 'Неправильний email'),
      username: (value: string) => (value.length > 4 ? null : 'Неправильний username, довжина має бути більше 4 символів'),
      password: (value: string) => (value.length > 7 ? null : 'Неправильний пароль, довжина має бути більше 7 символів'),
    },
  });

  const router = useRouter()

  const onSubmit = async (values: { username: string; email: string, password: string }) => {
    try {
      const data = await ApiService.register(values.username, values.email, values.password);
      console.log('Registration successful:', data);
      localStorage.setItem('userId', data.id);
      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);
      router.push('/events');
    } catch (error) {
      console.error('Registration error:', error);
      window.alert('Registration error: ' + error);
    }
  };

  return (
      <div className="bg-orange" style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        width: "100%"
      }}>
        <Container style={{width: "100%"}}>
          <Paper p="xl" style={{width: "100%", height: "auto", borderRadius: "1.5rem"}}>
            <form onSubmit={form.onSubmit(onSubmit)}>
              <Stack>
                <Center>
                  <Text size="xl" fw={500}>
                    РЕЄСТРАЦІЯ
                  </Text>
                </Center>

                <TextInput
                    withAsterisk
                    label="Ваш username"
                    placeholder="username"
                    {...form.getInputProps('username')}
                    mb='md'
                    required
                />
                <TextInput
                    withAsterisk
                    label="Ваш email"
                    placeholder="email"
                    {...form.getInputProps('email')}
                    mb='md'
                    required
                />

                  <PasswordInput
                      withAsterisk
                      label="Ваш пароль"
                      placeholder="Ваш пароль"
                      key={form.key('password')}
                      {...form.getInputProps('password')}
                      required
                  />

                <Button type="submit" fullWidth color="dark" size="md" radius="sm">
                  ЗАРЕЄСТРУВАТИСЬ
                </Button>

                <Center>
                  <Text size="sm" color="dimmed">
                    Вже маєте аккаунт?{" "}
                    <Anchor onClick={() => router.push('/sign-in')}>
                      ВХІД ДО КАБІНЕТУ
                    </Anchor>
                  </Text>
                </Center>
              </Stack>
            </form>
          </Paper>
        </Container>
      </div>
  );
}
