import { View, Text } from "react-native";

export default function NearbyScreen() {
  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-lg font-bold">근처 피드</Text>
      <Text className="mt-2 text-gray-500">주변 후기가 여기에 표시됩니다.</Text>
    </View>
  );
}
