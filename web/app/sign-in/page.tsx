'use client'
import {useForm} from '@mantine/form';
import {useRouter} from 'next/navigation'
import {
    TextInput,
    PasswordInput,
    Checkbox,
    Button,
    Text,
    Anchor,
    Paper,
    Stack,
    Center
} from "@mantine/core";


export default function SignInPage() {
    const form = useForm({
        mode: 'uncontrolled',
        initialValues: {
            username: '',
            password: '',
        },

        validate: {
            username: (value: string) => (value.length > 4 ? null : 'Incorrect username, the length must be more than 4 characters'),
            password: (value: string) => (value.length > 7 ? null : 'Incorrect password, the length must be more than 7 characters'),
        },
    });

    const router = useRouter()

    const onSubmit = async (values: { username: string; password: string }) => {
        try {
            const res = await fetch(`/api/auth/signin`, {
                method: 'POST',
                body: JSON.stringify({ username: values.username, password: values.password }),
            });

            if (!res.ok) {
                const err = await res.json();
                throw new Error(err.error || "Failed to sign in");
            }
            const data = await res.json();

            console.log('Login successful:', data);
            localStorage.setItem('userId', data.id);
            localStorage.setItem('accessToken', data.accessToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            router.push('/events');
        } catch (error) {
            console.error('Login error:', error);
            window.alert('Login error: ' + error);
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
                                    LOGIN TO YOUR ACCOUNT
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

                            <div>
                                <div style={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
                                    <Text size="sm" fw={500}>
                                        Password
                                    </Text>
                                    <Anchor href="#" size="xs">
                                        Forgot your password?
                                    </Anchor>
                                </div>

                                <PasswordInput
                                    withAsterisk
                                    placeholder="Password"
                                    key={form.key('password')}
                                    {...form.getInputProps('password')}
                                    required
                                />
                            </div>

                            <Checkbox
                                label="Remember me"
                            />

                            <Button type="submit" fullWidth color="dark" size="md" radius="sm">
                                Sign in
                            </Button>

                            <Center>
                                <Text size="sm" color="dimmed">
                                    Don&#39t have an account?{" "}
                                    <Anchor onClick={() => router.push('/sign-up')}>
                                        SIGN UP
                                    </Anchor>
                                </Text>
                            </Center>
                        </Stack>
                    </form>
                </Paper>
    );
}
