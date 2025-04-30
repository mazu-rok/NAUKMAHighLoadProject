'use client'
import {useEffect, useState} from 'react';
import {useRouter} from 'next/navigation'
import {
    Table,
    Title,
    Button,
    Text,
    Paper,
    Stack, Modal,
} from "@mantine/core";
import {Order} from '@/components/types/order';

interface User {
    username: string;
    email: string;
}

export default function ProfilePage() {
    const [user, setUser] = useState<User | null>(null);
    const [orders, setOrders] = useState<Order[]>([]);
    const [opened, setOpened] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
    const handleOpenModal = (order: Order) => {
        setSelectedOrder(order);
        setOpened(true);
    };
    const router = useRouter()

    const loadUserInfo = async () => {
        try {
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

            const res = await fetch(`/api/buckets/${localStorage.getItem('userId')}/orders`, {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                },
            });
            if (!res.ok) throw new Error('Failed to load orders');
            const data = await res.json();

            console.log(data);
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
            <Modal
                opened={opened}
                onClose={() => setOpened(false)}
                title="Order details"
            >
                {selectedOrder && (
                    <>
                        <Text>Order ID: {selectedOrder.id}</Text>
                        <Text>Event name: {selectedOrder.eventName}</Text>
                        <Text>Date: {selectedOrder.createdAt.slice(0, 16).replace('T', ' ')}</Text>

                        <Text mt="md" fw={600}>Tickets:</Text>
                        <Table variant="vertical" layout="fixed" withTableBorder>
                            <Table.Tbody>
                                {selectedOrder.tickets?.map((ticket, index) => (
                                    <Table.Tr key={index}>
                                        <Table.Th w={160}>{index + 1}</Table.Th>
                                        <Table.Td>Row: {ticket.row}, Place: {ticket.place}</Table.Td>
                                    </Table.Tr>
                                ))}
                            </Table.Tbody>
                        </Table>
                    </>
                )}
            </Modal>

            <Stack gap="xs" align="stretch" style={{width: "40%", minWidth: "600px", height: "100%", padding: "20px"}}>
                <Title order={1}>Profile</Title>
                <Text size="xl" style={{ marginLeft: "20px" }} fw={600}>Username: {user?.username || 'Loading...'}</Text>
                <Text size="xl" style={{ marginLeft: "20px" }} fw={600}> Email: {user?.email || 'Loading...'}</Text>

                <Title order={2}>Orders</Title>
                <Table striped>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>ID</Table.Th>
                            <Table.Th>Event name</Table.Th>
                            <Table.Th>Date</Table.Th>
                            <Table.Th></Table.Th>
                        </Table.Tr>
                    </Table.Thead>
                    <Table.Tbody>
                        {orders.map((order) => (
                            <Table.Tr key={order.id}>
                                <Table.Td>{order.id}</Table.Td>
                                <Table.Td>{order.eventName}</Table.Td>
                                <Table.Td>{order.createdAt.slice(0, 16).replace('T', ' ')}</Table.Td>
                                <Table.Td style={{textAlign: 'right'}}>
                                    <Button size="xs" variant="light"
                                            onClick={() => handleOpenModal(order)}>
                                        Details
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
