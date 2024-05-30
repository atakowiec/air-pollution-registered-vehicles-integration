import style from "../Home.module.scss";
import PolandMap from "./PolandMap.tsx";
import { Row, Table } from "react-bootstrap";
import { formatNumber } from "../../../util/utils.ts";
import ChartStatusOverlay, { PropsWithApiData } from "./ChartStatusOverlay.tsx";
import { useState } from "react";
import useApi from "../../../hooks/useApi.ts";

const INDICATORS = ["NO2", "NOx", "PM2.5", "Pb(PM10)", "SO2"];
const YEARS = Array.from({ length: 24 }, (_, i) => 2000 + i);
const VOIVODESHIPS = {
  "02": "dolnośląskie",
  "04": "kujawsko-pomorskie",
  "06": "lubelskie",
  "08": "lubuskie",
  "10": "łódzkie",
  "12": "małopolskie",
  "14": "mazowieckie",
  "16": "opolskie",
  "18": "podkarpackie",
  "20": "podlaskie",
  "22": "pomorskie",
  "24": "śląskie",
  "26": "świętokrzyskie",
  "28": "warmińsko-mazurskie",
  "30": "wielkopolskie",
  "32": "zachodniopomorskie",
  XX: "nieokreślone",
};

// todo
//przerobic, by mozna bylo wybrac tylko rok i w tabeli wszystkie dane sie wyswietla
//przerobic pierwiastki by nie byly select tylko tablica z wybiraniem i mozliwosci usuwania
//zrobic dobre wykresy

export default function MapAndTable({ apiData }: PropsWithApiData) {
  const [selectedIndicator, setSelectedIndicator] = useState(INDICATORS[0]);
  const [selectedYear, setSelectedYear] = useState(YEARS[0]);
  const [selectedVoivodeship, setSelectedVoivodeship] = useState("");

  const airPollutionData = useApi(
    `/air-pollution/counts/average-by-indicator-and-year?indicator=${selectedIndicator}&year=${selectedYear}`,
    "get"
  );

  const handleIndicatorChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    setSelectedIndicator(event.target.value);
  };

  const handleYearChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedYear(Number(event.target.value));
  };

  const findAirPollution = (voivodeship: string) => {
    if (!airPollutionData.data) {
      return "N/A";
    }
  
    const data = airPollutionData.data.find(
      ([name]: [string, number]) => name === voivodeship
    );
    return data ? data[1] : "N/A";
  };

  const pollutionData = apiData.data 
  ? (Object.keys(apiData.data) as Array<keyof typeof VOIVODESHIPS>).reduce((acc, key) => {
      const voivodeshipName = VOIVODESHIPS[key];
      acc[voivodeshipName] = {
        name: voivodeshipName,
        averagePollution: findAirPollution(voivodeshipName)
      };
      return acc;
    }, {} as Record<string, { name: string, averagePollution: number | 'N/A' }>)
  : {};
  
  console.log(pollutionData);

  return (
    <>
      <Row>
        <div className="container mb-3">
          <div className="row align-items-center">
            <div className="col-auto text-end pe-3">
              <p className="mb-0">Pierwiastek:</p>
            </div>
            <div className="col-auto text-center">
              <div className="">
                <select
                  value={selectedIndicator}
                  onChange={handleIndicatorChange}
                  className="form-select"
                >
                  {INDICATORS.map((indicator) => (
                    <option key={indicator} value={indicator}>
                      {indicator}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div className="col-auto text-end pe-3">
              <p className="mb-0">Rok:</p>
            </div>
            <div className="col-auto text-center">
              <div className="">
                <select
                  value={selectedYear}
                  onChange={handleYearChange}
                  className="form-select"
                >
                  {YEARS.map((year) => (
                    <option key={year} value={year}>
                      {year}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        </div>
      </Row>
      <Row>
        <div
          className={`text-center col-12 col-xxl-5 ${style.map} position-relative`}
        >
          <ChartStatusOverlay apiData={apiData} backgroundType={"none"} />
          <PolandMap apiData={apiData} pollutionData={pollutionData}/>
        </div>
        <div className={`col-12 col-xxl-7 ${style.table}`}>
          <Table bordered striped={apiData.data ? true : undefined}>
            <thead>
              <tr>
                <th>Województwo</th>
                <th>Liczba pojazdów</th>
                <th>Średnie zanieczyszczenie {selectedIndicator}</th>
              </tr>
            </thead>
            <tbody>
              {apiData.data
                ? (
                    Object.keys(apiData.data) as Array<
                      keyof typeof VOIVODESHIPS
                    >
                  ).map((key) => {
                    const voivodeshipName = VOIVODESHIPS[key];
                    return (
                      <tr key={key}>
                        <td>{voivodeshipName}</td>
                        <td>{formatNumber(apiData.data[key])}</td>
                        <td>{findAirPollution(voivodeshipName)}</td>
                      </tr>
                    );
                  })
                : Array.from({ length: 10 }, (_, i) => (
                    <LoadingRow key={i} i={i} />
                  ))}
            </tbody>
          </Table>
        </div>
      </Row>
    </>
  );
}

function LoadingRow({ i }: { i: number }) {
  return (
    <tr>
      <td
        className={style.loadingRow}
        style={{ animationDelay: `${i * 0.1}s` }}
      >
        ...
      </td>
      <td
        className={style.loadingRow}
        style={{ animationDelay: `${i * 0.1 + 0.1}s` }}
      >
        ...
      </td>
      <td
        className={style.loadingRow}
        style={{ animationDelay: `${i * 0.1 + 0.2}s` }}
      >
        ...
      </td>
    </tr>
  );
}
