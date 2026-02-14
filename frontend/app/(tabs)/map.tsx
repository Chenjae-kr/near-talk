import { View, Text } from "react-native";

export default function MapScreen() {
  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-lg font-bold">지도</Text>
      <Text className="mt-2 text-gray-500">
        네이버 지도가 여기에 표시됩니다.
      </Text>
      <Text className="mt-1 text-xs text-gray-400">
        (개발 빌드에서만 동작합니다)
      </Text>
    </View>
  );
}
