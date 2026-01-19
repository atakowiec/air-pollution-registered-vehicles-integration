import {useParams, Link, useNavigate} from "react-router-dom";
import {Container, Card, ListGroup, Button, Form} from "react-bootstrap";
import {MainNavbar} from "../../components/MainNavbar.tsx";
import {title} from "../../util/title.ts";
import useApi from "../../hooks/useApi.ts";
import {Vehicle} from "../../types/vehicle.ts";
import React, {useState, useEffect} from "react";
import {getApi} from "../../axios/axios.ts";

export default function VehicleDetails() {
    const {id} = useParams<{ id: string }>();
    const navigate = useNavigate();
    title(`Pojazd ${id}`);

    const {data: vehicle, loaded, setData} = useApi<Vehicle>(`/vehicles/${id}`, 'get');
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState<Partial<Vehicle>>({});

    useEffect(() => {
        if (vehicle) {
            setFormData(vehicle);
        }
    }, [vehicle]);

    if (!loaded) {
        return (
            <>
                <MainNavbar />
                <Container className="mt-5">Ładowanie...</Container>
            </>
        );
    }

    if (!vehicle) {
        return (
            <>
                <MainNavbar />
                <Container className="mt-5">Pojazd nie został znaleziony.</Container>
            </>
        );
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSave = async () => {
        try {
            const response = await (getApi() as any).patch(`/vehicles/${id}`, formData);
            if (response.status === 200) {
                setData?.(response.data);
                setIsEditing(false);
            }
        } catch (error) {
            console.error("Error updating vehicle:", error);
            alert("Nie udało się zaktualizować pojazdu.");
        }
    };

    const handleDelete = async () => {
        if (window.confirm("Czy na pewno chcesz usunąć ten pojazd?")) {
            try {
                const response = await (getApi() as any).delete(`/vehicles/${id}`);
                if (response.status === 200) {
                    navigate("/vehicles");
                }
            } catch (error) {
                console.error("Error deleting vehicle:", error);
                alert("Nie udało się usunąć pojazdu.");
            }
        }
    };

    return (
        <>
            <MainNavbar />
            <Container className="mt-5 mb-5">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <Button as={Link} to="/vehicles" variant="secondary">Powrót do listy</Button>
                    <div>
                        {!isEditing ? (
                            <>
                                <Button variant="primary" className="me-2" onClick={() => setIsEditing(true)}>Edytuj</Button>
                                <Button variant="danger" onClick={handleDelete}>Usuń</Button>
                            </>
                        ) : (
                            <>
                                <Button variant="success" className="me-2" onClick={handleSave}>Zapisz</Button>
                                <Button variant="secondary" onClick={() => setIsEditing(false)}>Anuluj</Button>
                            </>
                        )}
                    </div>
                </div>
                <Card>
                    <Card.Header as="h5">
                        {isEditing ? "Edytuj pojazd" : `${vehicle.brand} ${vehicle.model} (${vehicle.manufactureYear})`}
                    </Card.Header>
                    <Card.Body>
                        {isEditing ? (
                            <Form>
                                <Form.Group className="mb-3">
                                    <Form.Label>Marka</Form.Label>
                                    <Form.Control name="brand" value={formData.brand || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Model</Form.Label>
                                    <Form.Control name="model" value={formData.model || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Rok produkcji</Form.Label>
                                    <Form.Control type="number" name="manufactureYear" value={formData.manufactureYear || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Kod obszaru</Form.Label>
                                    <Form.Control name="areaCode" value={formData.areaCode || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Rodzaj paliwa</Form.Label>
                                    <Form.Control name="fuelType" value={formData.fuelType || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>ID pojazdu</Form.Label>
                                    <Form.Control name="vehicleId" value={formData.vehicleId?.toString() || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Kod powiatu</Form.Label>
                                    <Form.Control name="countyCode" value={formData.countyCode || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Typ</Form.Label>
                                    <Form.Control name="type" value={formData.type || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Podtyp</Form.Label>
                                    <Form.Control name="subType" value={formData.subType || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Pojemność silnika</Form.Label>
                                    <Form.Control type="number" name="engineCapacity" value={formData.engineCapacity || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Moc silnika</Form.Label>
                                    <Form.Control type="number" name="enginePower" value={formData.enginePower || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Masa własna</Form.Label>
                                    <Form.Control type="number" name="curbWeight" value={formData.curbWeight || ''} onChange={handleInputChange} />
                                </Form.Group>
                                <Form.Group className="mb-3">
                                    <Form.Label>Metoda produkcji</Form.Label>
                                    <Form.Control name="manufactureMethod" value={formData.manufactureMethod || ''} onChange={handleInputChange} />
                                </Form.Group>
                            </Form>
                        ) : (
                            <ListGroup variant="flush">
                                <ListGroup.Item><strong>ID:</strong> {vehicle.id}</ListGroup.Item>
                                <ListGroup.Item><strong>ID pojazdu:</strong> {vehicle.vehicleId}</ListGroup.Item>
                                <ListGroup.Item><strong>Kod obszaru:</strong> {vehicle.areaCode}</ListGroup.Item>
                                <ListGroup.Item><strong>Kod powiatu:</strong> {vehicle.countyCode}</ListGroup.Item>
                                <ListGroup.Item><strong>Typ:</strong> {vehicle.type}</ListGroup.Item>
                                <ListGroup.Item><strong>Podtyp:</strong> {vehicle.subType}</ListGroup.Item>
                                <ListGroup.Item><strong>Data pierwszej rejestracji:</strong> {vehicle.firstRegistrationDate}</ListGroup.Item>
                                <ListGroup.Item><strong>Pojemność silnika:</strong> {vehicle.engineCapacity} cm³</ListGroup.Item>
                                <ListGroup.Item><strong>Moc silnika:</strong> {vehicle.enginePower} kW</ListGroup.Item>
                                <ListGroup.Item><strong>Rodzaj paliwa:</strong> {vehicle.fuelType}</ListGroup.Item>
                                <ListGroup.Item><strong>Masa własna:</strong> {vehicle.curbWeight} kg</ListGroup.Item>
                                <ListGroup.Item><strong>Metoda produkcji:</strong> {vehicle.manufactureMethod}</ListGroup.Item>
                            </ListGroup>
                        )}
                    </Card.Body>
                </Card>
            </Container>
        </>
    );
}
