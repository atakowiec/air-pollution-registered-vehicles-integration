import React, {useState} from 'react';
import {Container, Card, Form, Button} from 'react-bootstrap';
import {MainNavbar} from "../../components/MainNavbar.tsx";
import {title} from "../../util/title.ts";
import {getApi} from "../../axios/axios.ts";
import {useNavigate, Link} from "react-router-dom";
import {Vehicle} from "../../types/vehicle.ts";

export default function AddVehicle() {
    title("Dodaj pojazd");
    const navigate = useNavigate();

    const [formData, setFormData] = useState<Partial<Vehicle>>({
        brand: '',
        model: '',
        manufactureYear: undefined,
        areaCode: '',
        fuelType: '',
        vehicleId: undefined,
        countyCode: '',
        type: '',
        subType: '',
        engineCapacity: undefined,
        enginePower: undefined,
        curbWeight: undefined,
        manufactureMethod: ''
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSave = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await (getApi() as any).post('/vehicles', formData);
            if (response.status === 200 || response.status === 201) {
                navigate(`/vehicles/${response.data.id}`);
            }
        } catch (error) {
            console.error("Error creating vehicle:", error);
            alert("Nie udało się dodać pojazdu.");
        }
    };

    return (
        <>
            <MainNavbar />
            <Container className="mt-5 mb-5">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h3>Dodaj nowy pojazd</h3>
                    <Button as={Link} to="/vehicles" variant="secondary">Powrót do listy</Button>
                </div>
                <Card>
                    <Card.Body>
                        <Form onSubmit={handleSave}>
                            <Form.Group className="mb-3">
                                <Form.Label>Marka</Form.Label>
                                <Form.Control name="brand" value={formData.brand || ''} onChange={handleInputChange} required />
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Model</Form.Label>
                                <Form.Control name="model" value={formData.model || ''} onChange={handleInputChange} required />
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
                                <Form.Control name="vehicleId" value={formData.vehicleId?.toString() || ''} onChange={handleInputChange} required />
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
                            <div className="d-flex justify-content-end">
                                <Button type="submit" variant="success">Dodaj pojazd</Button>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            </Container>
        </>
    );
}
