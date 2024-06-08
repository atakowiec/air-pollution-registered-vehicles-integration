import {createContext, ReactNode, useMemo} from "react";
import useApi, {ApiData} from "../../../hooks/useApi.ts";
import getCumulativeData from "./getCumulativeData.ts";

export const HomeDataContext = createContext<ContextPayload | undefined>(undefined);

export interface ContextPayload {
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
  "PM2.5"?: number;
  "Pb(PM10)"?: number;
  SO2?: number;
}

export function HomeDataContextProvider({children}: { children: ReactNode }) {
  const mergedData = useApi("/data/counts/by-year-and-voivodeships", "get");

  // useMemo is used to prevent unnecessary recalculations
  const cumulativeData = useMemo(() => getCumulativeData(mergedData), [mergedData]);

  return (
    <HomeDataContext.Provider value={{
      mergedData,
      cumulativeData
    }}>
      {children}
    </HomeDataContext.Provider>
  );
}