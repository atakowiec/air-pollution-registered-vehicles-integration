import React, {useState, useRef} from 'react';
import {Container, Table, Form, Row, Col, Button, Pagination} from 'react-bootstrap';
import {MainNavbar} from "../../components/MainNavbar.tsx";
import {title} from "../../util/title.ts";
import useApi from "../../hooks/useApi.ts";
import {Page, Vehicle} from "../../types/vehicle.ts";
import {Link} from "react-router-dom";

export default function VehiclesTable() {
    title("Pojazdy");

    const [page, setPage] = useState(0);

    // Refs for input values to avoid re-renders on every keystroke
    const areaCodeRef = useRef<HTMLInputElement>(null);
    const brandRef = useRef<HTMLInputElement>(null);
    const modelRef = useRef<HTMLInputElement>(null);
    const manufactureYearRef = useRef<HTMLInputElement>(null);
    const fuelTypeRef = useRef<HTMLInputElement>(null);

    // State that actually triggers the API call
    const [searchParams, setSearchParams] = useState({
        areaCode: '',
        brand: '',
        model: '',
        manufactureYear: '' as number | '',
        fuelType: ''
    });

    const queryParams = new URLSearchParams();
    queryParams.append('page', page.toString());
    queryParams.append('size', '100');
    if (searchParams.areaCode) queryParams.append('areaCode', searchParams.areaCode);
    if (searchParams.brand) queryParams.append('brand', searchParams.brand);
    if (searchParams.model) queryParams.append('model', searchParams.model);
    if (searchParams.manufactureYear) queryParams.append('manufactureYear', searchParams.manufactureYear.toString());
    if (searchParams.fuelType) queryParams.append('fuelType', searchParams.fuelType);

    const {data: vehiclesPage, loaded} = useApi<Page<Vehicle>>(`/vehicles?${queryParams.toString()}`, 'get');

    const handleSearch = (e: React.FormEvent) => {
        e.preventDefault();
        setSearchParams({
            areaCode: areaCodeRef.current?.value || '',
            brand: brandRef.current?.value || '',
            model: modelRef.current?.value || '',
            manufactureYear: manufactureYearRef.current?.value ? parseInt(manufactureYearRef.current.value) : '',
            fuelType: fuelTypeRef.current?.value || ''
        });
        setPage(0);
    };

    const renderPagination = () => {
        if (!vehiclesPage || vehiclesPage.totalPages <= 1) return null;

        const items = [];
        const current = vehiclesPage.number;
        const total = vehiclesPage.totalPages;

        const start = Math.max(0, current - 2);
        const end = Math.min(total - 1, current + 2);

        if (start > 0) {
            items.push(<Pagination.First key="first" onClick={() => setPage(0)} />);
            items.push(<Pagination.Ellipsis key="start-ellipsis" disabled />);
        }

        for (let number = start; number <= end; number++) {
            items.push(
                <Pagination.Item key={number} active={number === current} onClick={() => setPage(number)}>
                    {number + 1}
                </Pagination.Item>
            );
        }

        if (end < total - 1) {
            items.push(<Pagination.Ellipsis key="end-ellipsis" disabled />);
            items.push(<Pagination.Last key="last" onClick={() => setPage(total - 1)} />);
        }

        return <Pagination>{items}</Pagination>;
    };

    return (
        <>
            <MainNavbar />
            <Container className="mt-5 mb-5">
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h3>Pojazdy</h3>
                    <Button as={Link} to="/vehicles/add" variant="success">Dodaj pojazd</Button>
                </div>
                <Form onSubmit={handleSearch} className="mb-4">
                    <Row>
                        <Col md={2} className="mb-2">
                            <Form.Control
                                placeholder="Kod obszaru"
                                ref={areaCodeRef}
                                defaultValue={searchParams.areaCode}
                            />
                        </Col>
                        <Col md={2} className="mb-2">
                            <Form.Control
                                placeholder="Marka"
                                ref={brandRef}
                                defaultValue={searchParams.brand}
                            />
                        </Col>
                        <Col md={2} className="mb-2">
                            <Form.Control
                                placeholder="Model"
                                ref={modelRef}
                                defaultValue={searchParams.model}
                            />
                        </Col>
                        <Col md={2} className="mb-2">
                            <Form.Control
                                type="number"
                                placeholder="Rok"
                                ref={manufactureYearRef}
                                defaultValue={searchParams.manufactureYear}
                            />
                        </Col>
                        <Col md={2} className="mb-2">
                            <Form.Control
                                placeholder="Rodzaj paliwa"
                                ref={fuelTypeRef}
                                defaultValue={searchParams.fuelType}
                            />
                        </Col>
                        <Col md={2}>
                            <Button type="submit" variant="primary" className="w-100">Szukaj</Button>
                        </Col>
                    </Row>
                </Form>

                {!loaded ? (
                    <div>Ładowanie...</div>
                ) : (
                    <>
                        <Table striped bordered hover responsive>
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Marka</th>
                                <th>Model</th>
                                <th>Rok</th>
                                <th>Kod obszaru</th>
                                <th>Rodzaj paliwa</th>
                                <th>Akcje</th>
                            </tr>
                            </thead>
                            <tbody>
                            {vehiclesPage?.content.map((vehicle) => (
                                <tr key={vehicle.id}>
                                    <td>{vehicle.id}</td>
                                    <td>{vehicle.brand}</td>
                                    <td>{vehicle.model}</td>
                                    <td>{vehicle.manufactureYear}</td>
                                    <td>{vehicle.areaCode}</td>
                                    <td>{vehicle.fuelType}</td>
                                    <td>
                                        <Link to={`/vehicles/${vehicle.id}`} className="btn btn-sm btn-info">Szczegóły</Link>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </Table>
                        <div className="d-flex justify-content-center">
                            {renderPagination()}
                        </div>
                        <div className="text-center text-muted">
                            Liczba elementów: {vehiclesPage?.totalElements.toLocaleString('pl-PL')}
                        </div>
                    </>
                )}
            </Container>
        </>
    );
}
