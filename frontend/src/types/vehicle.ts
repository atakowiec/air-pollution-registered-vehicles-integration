export interface Vehicle {
    id: number;
    vehicleId: number;
    areaCode: string;
    countyCode: string;
    brand: string;
    model: string;
    type: string;
    subType: string;
    manufactureYear: number;
    manufactureMethod: string;
    firstRegistrationDate: string;
    engineCapacity: number;
    enginePower: number;
    hybridEnginePower: number | null;
    curbWeight: number;
    fuelType: string;
    alternativeFuelType: string | null;
    alternativeFuelType2: string | null;
    averageFuelConsumption: number | null;
    deregistrationDate: string | null;
    vehiclesOwnerArea: string;
    fuelCo2Emission: number | null;
    alternativeFuelCo2Emission: number | null;
}

export interface Page<T> {
    content: T[];
    pageable: {
        pageNumber: number;
        pageSize: number;
        sort: {
            empty: boolean;
            sorted: boolean;
            unsorted: boolean;
        };
        offset: number;
        unpaged: boolean;
        paged: boolean;
    };
    last: boolean;
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    sort: {
        empty: boolean;
        sorted: boolean;
        unsorted: boolean;
    };
    numberOfElements: number;
    first: boolean;
    empty: boolean;
}
