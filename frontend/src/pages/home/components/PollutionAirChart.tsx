import React, { useMemo, useState } from "react";
import {
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  AreaChart,
  Area,
} from "recharts";
import style from "../Home.module.scss";
import ChartStatusOverlay, { PropsWithApiData } from "./ChartStatusOverlay.tsx";
import {round} from "../../../util/utils.ts";

export default function PollutionAirChart({ apiData }: PropsWithApiData) {
  const [selectedProvince, setSelectedProvince] = useState("dolnośląskie");

  const years = useMemo(() => Object.keys(apiData.data || {}).sort(), [apiData.data]);

  const provinces = useMemo(() => {
    const firstYear = years[0];
    return firstYear ? Object.keys(apiData.data[firstYear]) : [];
  }, [years, apiData.data]);

  const handleProvinceChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedProvince(event.target.value);
  };

  const pollutionData = useMemo(() => {
    if (!selectedProvince || !apiData.data) return [];
    return years.map((year) => ({
      year,
      NO2: round(apiData.data[year]?.[selectedProvince]?.NO2 || 0),
      NOx: round(apiData.data[year]?.[selectedProvince]?.NOx || 0),
    }));
  }, [selectedProvince, apiData.data, years]);

  return (
    <div className={`col-12 col-xl-6 ${style.chartContainer}`}>
      <h3 className={style.chartTitle}>Wykres liniowy dla zanieczyszczenia NO2 i NOx w wybranym województwie</h3>
      <div>
        <select value={selectedProvince} onChange={handleProvinceChange} className="form-select m-2 ">
          <option value="">Wybierz województwo</option>
          {provinces.map((province) => (
            <option key={province} value={province}>
              {province}
            </option>
          ))}
        </select>
      </div>
      <ResponsiveContainer width={"100%"} height={300}>
        <AreaChart
          width={730}
          height={250}
          data={pollutionData}
          margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
        >
          <defs>
            <linearGradient id="colorUv" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#8884d8" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#8884d8" stopOpacity={0} />
            </linearGradient>
            <linearGradient id="colorNOx" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#82ca9d" stopOpacity={0.8} />
              <stop offset="95%" stopColor="#82ca9d" stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis dataKey="year" />
          <YAxis />
          <CartesianGrid strokeDasharray="3 3" />
          <Tooltip />
          <Area
            type="monotone"
            dataKey="NO2"
            stroke="#8884d8"
            fillOpacity={1}
            fill="url(#colorUv)"
          />
           <Area
            type="monotone"
            dataKey="NOx"
            stroke="#82ca9d"
            fillOpacity={1}
            fill="url(#colorNOx)"
          />
        </AreaChart>
      </ResponsiveContainer>
      <ChartStatusOverlay apiData={apiData}/>
    </div>
  );
}
