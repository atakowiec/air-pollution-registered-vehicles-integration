import {useContext} from "react";
import {HomeDataContext} from "./HomeDataContext.tsx";

export function useMergedData() {
  return useContext(HomeDataContext)?.mergedData!
}

export function useCumulativeData() {
  return useContext(HomeDataContext)?.cumulativeData!
}

export function useHomeData() {
  return useContext(HomeDataContext)!
}