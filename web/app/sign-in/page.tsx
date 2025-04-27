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
import { ApiService } from '@/services/ApiService';


export default function SignInPage() {
    const form = useForm({
        mode: 'uncontrolled',
        initialValues: {
            username: '',
            password: '',
        },

        validate: {
            username: (value: string) => (value.length > 4 ? null : 'Неправильний username, довжина має бути більше 4 символів'),
            password: (value: string) => (value.length > 7 ? null : 'Неправильний пароль, довжина має бути більше 7 символів'),
        },
    });

    const router = useRouter()

    const onSubmit = async (values: { username: string; password: string }) => {
        try {
            const data = await ApiService.login(values.username, values.password);
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
                <Paper p="xl" style={{width: "100%", height: "100%",borderRadius: '80px'}}>
                    <form onSubmit={form.onSubmit(onSubmit)}>
                        <Stack>
                            <Center>
                                <Text size="xl" fw={500}>
                                    ВХІД ДО КАБІНЕТУ
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

                            <div>
                                <div style={{display: "flex", justifyContent: "space-between", alignItems: "center"}}>
                                    <Text size="sm" fw={500}>
                                        Ваш пароль
                                    </Text>
                                    <Anchor href="#" size="xs">
                                        Забули пароль?
                                    </Anchor>
                                </div>

                                <PasswordInput
                                    withAsterisk
                                    placeholder="Ваш пароль"
                                    key={form.key('password')}
                                    {...form.getInputProps('password')}
                                    required
                                />
                            </div>

                            <Checkbox
                                label="Запам’ятати мене"
                            />

                            <Button type="submit" fullWidth color="dark" size="md" radius="sm">
                                УВІЙТИ
                            </Button>

                            <Center>
                                <Text size="sm" color="dimmed">
                                    Не маєте акаунта?{" "}
                                    <Anchor onClick={() => router.push('/sign-up')}>
                                        ЗАРЕЄСТРУЙТЕСЯ
                                    </Anchor>
                                </Text>
                            </Center>
                        </Stack>
                    </form>
                </Paper>
    );
}
