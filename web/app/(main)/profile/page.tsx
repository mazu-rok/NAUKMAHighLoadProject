'use client'
import {useEffect, useState} from 'react';
import {useRouter} from 'next/navigation'
import {
    Table,
    Title,
    Button,
    Text,
    Paper,
    Stack,
} from "@mantine/core";

interface User {
    username: string;
    email: string;
}

interface Order {
    orderId: string;
    eventName: string;
    date: string;
}

export default function ProfilePage() {
    const [user, setUser] = useState<User | null>(null);
    const [orders, setOrders] = useState<Order[]>([]);
    const router = useRouter()

    const loadUserInfo = async () => {
        try {
            console.log('Loading user...');
            const res = await fetch(`/api/users/${localStorage.getItem('userId')}`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                }
            });
            if (!res.ok) throw new Error('Failed to load user');
            const data = await res.json();
            setUser(data);
        } catch (error) {
            console.error('Error loading user:', error);
        }
    };

    const loadOrders = async () => {
        try {
            const res = await fetch('/api/orders', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                }
            });
            if (!res.ok) throw new Error('Failed to load orders');
            const data = await res.json();
            console.log('Loading orders...', data);

            setOrders(data);
        } catch (error) {
            console.error('Error loading orders:', error);
        }
    };

    useEffect(() => {
        const accessToken = localStorage.getItem('accessToken');

        if (accessToken == null) {
            router.push('/sign-in');
        } else {
            loadUserInfo();
            loadOrders();
        }
    }, []);

    return (
        <Paper p="xl" style={{
            width: "100%", height: "auto", minHeight: '100px', borderRadius: '80px', display: "flex",
            alignItems: "center",
            justifyContent: "center",
        }}>
            <Stack gap="xs" align="stretch" style={{width: "40%", minWidth: "600px", height: "100%", padding: "20px"}}>
                <Title order={1}>Мій профіль</Title>
                <Text size="xl" style={{ marginLeft: "20px" }} fw={600}>Username: {user?.username || 'Loading...'}</Text>
                <Text size="xl" style={{ marginLeft: "20px" }} fw={600}> Email: {user?.email || 'Loading...'}</Text>

                <Title order={2}>Замовлення</Title>
                <Table striped>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>ID</Table.Th>
                            <Table.Th>Назва події</Table.Th>
                            <Table.Th>Дата</Table.Th>
                            <Table.Th></Table.Th>
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>
                        {orders.map((order) => (
                            <Table.Tr key={order.orderId}>
                                <Table.Td>{order.orderId}</Table.Td>
                                <Table.Td>{order.eventName}</Table.Td>
                                <Table.Td>{order.date}</Table.Td>
                                <Table.Td style={{textAlign: 'right'}}>
                                    <Button size="xs" variant="light"
                                            onClick={() => console.log(`Go to order ${order.orderId}`)}>
                                        Деталі
                                    </Button>
                                </Table.Td>
                            </Table.Tr>
                        ))}
                    </Table.Tbody>
                </Table>
            </Stack>
        </Paper>
    );
}
