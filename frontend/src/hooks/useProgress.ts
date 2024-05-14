import {Dispatch, SetStateAction, useEffect, useState} from "react";
import {getApi} from "../axios/axios.ts";

export interface ProgressData<T> {
  key: string,
  status: ProgressStatus,
  data?: T
}

export type ProgressStatus = "NOT_STARTED" | "IN_PROGRESS" | "FINISHED" | "FAILED"

export default function useProgress<T>(key: string): [ProgressData<T> | undefined, Dispatch<SetStateAction<ProgressData<T> | undefined>>] {
  const [data, setData] = useState<ProgressData<T>>()

  useEffect(() => {
    const fetchData = () => {
      getApi().get(`/progress/${key}`)
        .then(response => setData(response.data))
    }

    fetchData()

    const interval = setInterval(fetchData, 1000);

    return () => clearInterval(interval);
  }, []);

  return [data, setData];
}