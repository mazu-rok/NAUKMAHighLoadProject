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
            email: (value: string) => (/^\S+@\S+$/.test(value) ? null : 'Incorrect email'),
            username: (value: string) => (value.length > 4 ? null : 'Incorrect username, the length must be more than 4 characters'),
            password: (value: string) => (value.length > 7 ? null : 'Incorrect password, the length must be more than 7 characters'),
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
                            REGISTRATION
                        </Text>
                    </Center>

                    <TextInput
                        withAsterisk
                        label="Username"
                        placeholder="Username"
                        {...form.getInputProps('username')}
                        mb='md'
                        required
                    />
                    <TextInput
                        withAsterisk
                        label="Email"
                        placeholder="Email"
                        {...form.getInputProps('email')}
                        mb='md'
                        required
                    />

                    <PasswordInput
                        withAsterisk
                        label="Password"
                        placeholder="Password"
                        key={form.key('password')}
                        {...form.getInputProps('password')}
                        required
                    />

                    <Button type="submit" fullWidth color="dark" size="md" radius="sm">
                        SIGN UP
                    </Button>

                    <Center>
                        <Text size="sm" color="dimmed">
                            Already have an account?{" "}
                            <Anchor onClick={() => router.push('/sign-in')}>
                                SIGN IN
                            </Anchor>
                        </Text>
                    </Center>
                </Stack>
            </form>
        </Paper>
    );
}
