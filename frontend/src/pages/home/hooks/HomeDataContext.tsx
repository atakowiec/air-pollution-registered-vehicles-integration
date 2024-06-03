import {createContext, ReactNode, useMemo} from "react";
import useApi, {ApiData} from "../../../hooks/useApi.ts";
import getMergedData from "./getMergedData.ts";
import getCumulativeData from "./getCumulativeData.ts";

export const HomeDataContext = createContext<ContextPayload | undefined>(undefined);

export interface ContextPayload {
  registrationsData: ApiData;
  deregistrationsData: ApiData;
  airPollutionData: ApiData;
  mergedData: ApiData<MergedData>
  cumulativeData: ApiData<MergedData>
}

export interface MergedData {
  [year: string]: YearData
}

export interface YearData {
  [voivodeship: string]: VoivodeshipData
}

export interface VoivodeshipData {
  registrations?: number;
  deregistrations?: number;
  NO2?: number;
  NOx?: number;
  PM2_5?: number;
  Pb_PM10?: number;
  SO2?: number;
}

export function HomeDataContextProvider({children}: { children: ReactNode }) {
  const registrationsData = useApi("/vehicles/counts/registrations-by-area-code-and-voivodeships", "get");
  const deregistrationsData = useApi("/vehicles/counts/deregistrations-by-area-code-and-voivodeships", "get");
  const airPollutionData = useApi("/air-pollution/counts/average-by-year-voivodeship-indicator", "get");

  // useMemo is used to prevent unnecessary recalculations
  const [mergedData, cumulativeData] = useMemo(() => [
    getMergedData(registrationsData, deregistrationsData, airPollutionData),
    getCumulativeData(registrationsData, deregistrationsData, airPollutionData)
  ], [registrationsData, deregistrationsData, airPollutionData]);

  return (
    <HomeDataContext.Provider value={{
      registrationsData,
      deregistrationsData,
      airPollutionData,
      mergedData,
      cumulativeData
    }}>
      {children}
    </HomeDataContext.Provider>
  );
}