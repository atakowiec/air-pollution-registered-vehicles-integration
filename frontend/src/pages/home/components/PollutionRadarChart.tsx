import React, { useMemo, useState } from "react";
import {
  ResponsiveContainer,
  Radar,
  PolarAngleAxis,
  PolarRadiusAxis,
  PolarGrid,
  RadarChart,
} from "recharts";
import style from "../Home.module.scss";
import { PropsWithApiData } from "./ChartStatusOverlay.tsx";

export default function PollutionRadarChart({ apiData }: PropsWithApiData) {
  const [selectedProvince, setSelectedProvince] = useState("dolnośląskie");
  const [selectedElement, setSelectedElement] = useState("NO2");

  const years = useMemo(() => Object.keys(apiData.data || {}).sort(), [apiData.data]);

  const provinces = useMemo(() => {
    const firstYear = years[0];
    return firstYear ? Object.keys(apiData.data[firstYear]) : [];
  }, [years, apiData.data]);

  const maxValue = useMemo(() => {
    let max = 0;
    years.forEach((year) => {
      Object.keys(apiData.data[year] || {}).forEach((province) => {
        const value = apiData.data[year][province]?.[selectedElement] || 0;
        if (value > max) {
          max = value;
        }
      });
    });
    return max;
  }, [selectedElement, apiData.data, years]);

  const handleProvinceChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedProvince(event.target.value);
  };

  const handleElementChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedElement(event.target.value);
  };

  const pollutionData = useMemo(() => {
    if (!selectedProvince || !apiData.data) return [];
    const selectedYears = years.slice(-5); 
    return selectedYears.map((year) => {
      const value = apiData.data[year]?.[selectedProvince]?.[selectedElement] || 0;
      return {
        year,
        value: (value / maxValue) * 100, 
      };
    });
  }, [selectedProvince, selectedElement, maxValue, apiData.data, years]);

  const radarData = useMemo(() => {
    return pollutionData.map(({ year, value }) => ({
      subject: year,
      value,
    }));
  }, [pollutionData]);

  return (
    <div className={"col-12 col-xl-6"}>
      <h3 className={style.chartTitle}>Wykres radarowy dla wybranego pierwiastka</h3>
      <div>
        <select value={selectedProvince} onChange={handleProvinceChange} className="form-select m-2">
          {provinces.map((province) => (
            <option key={province} value={province}>
              {province}
            </option>
          ))}
        </select>
        <select value={selectedElement} onChange={handleElementChange} className="form-select m-2">
          <option value="NO2">NO2</option>
          <option value="NOx">NOx</option>
          <option value="PM2.5">PM2.5</option>
          <option value="Pb(PM10)">Pb(PM10)</option>
          <option value="SO2">SO2</option>
        </select>
      </div>
      <ResponsiveContainer width={"100%"} height={300}>
        <RadarChart outerRadius={90} width={730} height={250} data={radarData}>
          <PolarGrid />
          <PolarAngleAxis dataKey="subject" />
          <PolarRadiusAxis angle={30} domain={[0, 100]} />
          <Radar
            name={selectedElement}
            dataKey="value"
            stroke="#8884d8"
            fill="#8884d8"
            fillOpacity={0.6}
          />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
}
