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
    Stack,
    Center,
} from "@mantine/core";


export default function SignUpPage() {
    const form = useForm({
        mode: 'uncontrolled',
        initialValues: {
            username: '',
            email: '',
            password: '',
        },

        validate: {
            email: (value: string) => (/^\S+@\S+$/.test(value) ? null : 'Неправильний email'),
            username: (value: string) => (value.length > 4 ? null : 'Неправильний username, довжина має бути більше 4 символів'),
            password: (value: string) => (value.length > 7 ? null : 'Неправильний пароль, довжина має бути більше 7 символів'),
        },
    });

    const router = useRouter()

    const onSubmit = async (values: { username: string; email: string, password: string }) => {
        try {
            const res = await fetch(`/api/auth/signup`, {
                method: 'POST',
                body: JSON.stringify({ username: values.username, email: values.email, password: values.password }),
            });

            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || "Failed to sign up");
            }
            const data = await res.json();
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
        <Paper p="xl" style={{
            width: "100%", height: "auto", borderRadius: '80px', display: "flex",
            alignItems: "center",
            justifyContent: "center",
        }}>
            <form onSubmit={form.onSubmit(onSubmit)} style={{width: "30%", minWidth: "600px", height: "100%", padding: "20px"}}>
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
    );
}
